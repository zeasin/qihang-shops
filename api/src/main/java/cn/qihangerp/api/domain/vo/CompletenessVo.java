package cn.qihangerp.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据完整度检查结果
 */
@Data
public class CompletenessVo implements Serializable {

    /**
     * 月份 yyyy-MM
     */
    private String month;

    /**
     * 本月订单数
     */
    private Long orderCount;

    /**
     * 未匹配商品成本的订单明细数
     */
    private Long unmatchedCostItemCount;

    /**
     * 涉及未匹配成本的订单数
     */
    private Long unmatchedCostOrderCount;

    /**
     * 本月推广费是否已录入
     */
    private Boolean promotionFeeEntered;

    /**
     * 本月固定费用是否已录入
     */
    private Boolean fixedFeeEntered;

    /**
     * 未匹配订单的账单明细数
     */
    private Long unmatchedBillCount;

    /**
     * 待处理工单数
     */
    private Long pendingWorkOrderCount;

    /**
     * 是否完整（无任何提示）
     */
    private Boolean complete;

    /**
     * 提示列表
     */
    private List<String> messages = new ArrayList<>();

    private static final long serialVersionUID = 1L;
}
