package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工单（售后支出记账）
 * @TableName erp_work_order
 */
@TableName(value = "erp_work_order")
@Data
public class ErpWorkOrder implements Serializable {

    /** 类型：补款 */
    public static final int TYPE_REISSUE_MONEY = 1;
    /** 类型：返现 */
    public static final int TYPE_CASHBACK = 2;
    /** 类型：补发 */
    public static final int TYPE_RESEND = 3;
    /** 类型：仅退款 */
    public static final int TYPE_REFUND_ONLY = 4;
    /** 类型：退货退款 */
    public static final int TYPE_RETURN_REFUND = 5;
    /** 类型：其他 */
    public static final int TYPE_OTHER = 6;

    /** 状态：待处理 */
    public static final int STATUS_PENDING = 0;
    /** 状态：已结算 */
    public static final int STATUS_SETTLED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单号（平台单号，可后补）
     */
    private String orderNum;

    /**
     * 商品ID（补发扣库存用）
     */
    private Long goodsId;

    /**
     * 类型 1补款 2返现 3补发 4仅退款 5退货退款 6其他
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 凭证图路径
     */
    private String voucher;

    /**
     * 状态 0待处理 1已结算
     */
    private Integer status;

    /**
     * 结算时间
     */
    private Date settleTime;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    /**
     * 规格ID（补发出库用，不落库）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Long skuId;

    /**
     * 补发数量（默认1，不落库）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer quantity;

    /**
     * 快递单号（查询用，不落库；按订单表 waybill_code 反查订单号）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String waybillCode;

    private static final long serialVersionUID = 1L;
}
