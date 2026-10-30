package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.common.ResultVo;
import cn.qihangerp.api.domain.ErpWorkOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 工单（售后支出记账）服务
 */
public interface ErpWorkOrderService extends IService<ErpWorkOrder> {

    /**
     * 分页查询（待处理置顶）
     */
    PageResult<ErpWorkOrder> queryPageList(ErpWorkOrder bo, PageQuery pageQuery);

    /**
     * 新建工单（速记/完整新建共用）；补发工单联动生成出库单
     *
     * @param operatorId 操作人ID
     * @param userName  操作人
     * @param workOrder 工单
     */
    ResultVo<Long> createWorkOrder(Long operatorId, String userName, ErpWorkOrder workOrder);
}
