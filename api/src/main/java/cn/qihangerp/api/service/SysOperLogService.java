package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.common.utils.AddressUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.SysOperLog;
import cn.qihangerp.api.mapper.SysOperLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 操作日志记录服务
 * <p>
 * 写入在独立单线程中异步执行，不阻塞业务请求；IP 归属地解析随写入线程完成。
 *
 * @author qihang
 */
@Slf4j
@Service
public class SysOperLogService
{
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "operlog-record");
        t.setDaemon(true);
        return t;
    });

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 异步记录操作日志（由 LogAspect 调用，记录失败不影响业务）
     *
     * @param operLog 待写入的日志（operLocation 可为空，入库前补齐归属地）
     */
    public void recordOperLog(SysOperLog operLog)
    {
        executor.execute(() -> {
            try
            {
                if (StringUtils.isEmpty(operLog.getOperLocation()))
                {
                    try
                    {
                        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                    }
                    catch (Exception e)
                    {
                        operLog.setOperLocation("未知");
                    }
                }
                operLogMapper.insert(operLog);
            }
            catch (Exception e)
            {
                log.error("操作日志入库失败: {}", operLog.getTitle(), e);
            }
        });
    }

    /**
     * 分页查询操作日志
     *
     * @param operLog   查询条件（模块标题模糊、操作人模糊、业务类型、状态、时间区间）
     * @param pageQuery 分页参数
     */
    public PageResult<SysOperLog> queryPageList(SysOperLog operLog, PageQuery pageQuery)
    {
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<SysOperLog>()
                .like(StringUtils.isNotEmpty(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
                .like(StringUtils.isNotEmpty(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
                .eq(operLog.getBusinessType() != null, SysOperLog::getBusinessType, operLog.getBusinessType())
                .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus())
                .ge(StringUtils.isNotEmpty(operLog.getBeginTime()), SysOperLog::getOperTime, operLog.getBeginTime())
                .le(StringUtils.isNotEmpty(operLog.getEndTime()), SysOperLog::getOperTime, endOfDay(operLog.getEndTime()));

        // 表主键为 oper_id，排序字段走白名单映射，未指定时按操作时间倒序
        String orderBy = pageQuery.getOrderByColumn();
        String column = switch (orderBy == null ? "" : orderBy) {
            case "operId" -> "oper_id";
            case "operName" -> "oper_name";
            case "costTime" -> "cost_time";
            default -> "oper_time";
        };
        pageQuery.setOrderByColumn(column);
        if (StringUtils.isEmpty(pageQuery.getIsAsc()))
        {
            pageQuery.setIsAsc("desc");
        }
        Page<SysOperLog> pages = operLogMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    /**
     * 批量删除操作日志
     */
    public int deleteOperLogByIds(List<Long> operIds)
    {
        return operLogMapper.deleteBatchIds(operIds);
    }

    /**
     * 清空全部操作日志
     */
    public void cleanOperLog()
    {
        operLogMapper.delete(Wrappers.emptyWrapper());
    }

    /**
     * 结束时间补齐到当天末尾，支持 2026-10-30 或完整日期时间两种入参
     */
    private String endOfDay(String endTime)
    {
        if (StringUtils.isEmpty(endTime))
        {
            return null;
        }
        return endTime.length() <= 10 ? endTime + " 23:59:59" : endTime;
    }
}
