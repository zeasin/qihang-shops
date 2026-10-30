package cn.qihangerp.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 链接利润（一行一款，推广费为分摊后）
 */
@Data
public class LinkProfitVo implements Serializable {

    /**
     * 链接/商品ID
     */
    private Long goodsId;

    /**
     * 链接名称
     */
    private String goodsName;

    /**
     * 销量
     */
    private Integer salesQty;

    /**
     * 实收（按商品金额占比分摊）
     */
    private BigDecimal payAmount;

    /**
     * 商品成本
     */
    private BigDecimal goodsCost;

    /**
     * 快递（按商品金额占比分摊）
     */
    private BigDecimal expressFee;

    /**
     * 平台扣费（按商品金额占比分摊）
     */
    private BigDecimal platformFee;

    /**
     * 售后成本（按商品金额占比分摊）
     */
    private BigDecimal afterSaleCost;

    /**
     * 分摊推广费（链接级直归 + 店铺级按实收占比）
     */
    private BigDecimal promotionFee;

    /**
     * 链接贡献利润 = 订单汇总 − 分摊推广费
     */
    private BigDecimal profit;

    /**
     * 利润率（%）
     */
    private BigDecimal profitRate;

    private static final long serialVersionUID = 1L;
}
