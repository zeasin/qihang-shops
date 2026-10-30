package cn.qihangerp.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺利润（一行一店，净利润）
 */
@Data
public class ShopProfitVo implements Serializable {

    private Long shopId;

    private String shopName;

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
     * 平台扣费
     */
    private BigDecimal platformFee;

    /**
     * 售后成本（已结算工单）
     */
    private BigDecimal afterSaleCost;

    /**
     * 推广费
     */
    private BigDecimal promotionFee;

    /**
     * 固定费用（人工/房租/软件/包装/其他）
     */
    private BigDecimal fixedFee;

    /**
     * 店铺净利润 = 链接贡献利润汇总 − 固定费用
     */
    private BigDecimal netProfit;

    /**
     * 利润率（%）
     */
    private BigDecimal profitRate;

    private static final long serialVersionUID = 1L;
}
