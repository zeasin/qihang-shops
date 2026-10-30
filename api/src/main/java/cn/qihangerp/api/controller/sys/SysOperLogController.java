package cn.qihangerp.api.controller.sys;

import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.TableDataInfo;
import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.security.SecurityUtils;
import cn.qihangerp.api.domain.SysOperLog;
import cn.qihangerp.api.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 操作日志记录
 *
 * @author qihang
 */
@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController extends BaseController
{
    @Autowired
    private SysOperLogService operLogService;

    /**
     * 查询操作日志列表（仅平台 admin）
     */
    @GetMapping("/list")
    public TableDataInfo list(SysOperLog operLog, PageQuery pageQuery)
    {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            return getDataTable(new ArrayList<>());
        }
        return getDataTable(operLogService.queryPageList(operLog, pageQuery));
    }

    /**
     * 批量删除操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            return error("没有权限");
        }
        return toAjax(operLogService.deleteOperLogByIds(Arrays.asList(operIds)));
    }

    /**
     * 清空操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            return error("没有权限");
        }
        operLogService.cleanOperLog();
        return success();
    }
}
