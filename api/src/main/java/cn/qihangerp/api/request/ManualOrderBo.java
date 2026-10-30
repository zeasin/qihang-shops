package cn.qihangerp.api.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 手动建单请求
 */
@Data
public class ManualOrderBo implements Serializable {

    private Long shopId;

    /**
     * 订单号（留空系统生成）
     */
    private String orderNum;

    /**
     * 下单时间 yyyy-MM-dd HH:mm:ss（留空取当前）
     */
    private String orderTime;

    /**
     * 实收金额
     */
    private Double payAmount;

    /**
     * 快递费
     */
    private Double expressFee;

    /**
     * 平台扣费（回退参考）
     */
    private Double platformFee;

    private String remark;

    private List<Item> itemList;

    @Data
    public static class Item implements Serializable {
        /**
         * 规格ID（erp_goods_sku.id）
         */
        private Long skuId;
        private Integer quantity;
        /**
         * 商品金额（小计）
         */
        private Double itemAmount;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
