package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.domain.vo.SalesDailyVo;
import cn.qihangerp.api.request.OrderSearchRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author TW
* @description 针对表【erp_order(订单表)】的数据库操作Service
* @createDate 2024-04-03 15:57:41
*/
public interface ErpOrderService extends IService<ErpOrder> {
    PageResult<ErpOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery);

    ErpOrder queryDetailById(Long id);

    List<SalesDailyVo> salesDaily();
    SalesDailyVo getTodaySalesDaily();
    Integer getWaitShipOrderAllCount();

    /**
     * 订单发货：校验待发货、写入发货成本/发货时间，并按当前商品成本档案写入明细成本快照
     */
    int shipOrders(String userName, List<String> ids, Double expressFee, String waybillCode);
}
