package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpPlatformBill;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 平台对账单明细服务
 */
public interface ErpPlatformBillService extends IService<ErpPlatformBill> {

    /**
     * 分页查询
     */
    PageResult<ErpPlatformBill> queryPageList(ErpPlatformBill bo, PageQuery pageQuery);

    /**
     * 按订单号自动匹配订单（未匹配 → 已匹配）
     *
     * @return 本次匹配上的笔数
     */
    int matchOrders();
}
