package cn.qihangerp.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单利润（一行一单）
 */
@Data
public class OrderProfitVo implements Serializable {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单号
     */
    private String orderNum;

    private Long shopId;

    private String shopName;

    private Long orderTime;

    /**
     * 实收
     */
    private BigDecimal payAmount;

    /**
     * 商品成本
     */
    private BigDecimal goodsCost;

    /**
     * 快递
     */
    private BigDecimal expressFee;

    /**
     * 平台扣费（账单匹配净额；无账单时为订单自带扣费参考）
     */
    private BigDecimal platformFee;

    /**
     * 平台扣费来源：bill=对账单  order=订单自带参考
     */
    private String platformFeeSource;

    /**
     * 售后成本（已结算工单汇总）
     */
    private BigDecimal afterSaleCost;

    /**
     * 订单利润 = 实收 − 商品成本 − 快递 − 平台扣费 − 售后成本
     */
    private BigDecimal profit;

    /**
     * 利润率（%）
     */
    private BigDecimal profitRate;

    private static final long serialVersionUID = 1L;
}
