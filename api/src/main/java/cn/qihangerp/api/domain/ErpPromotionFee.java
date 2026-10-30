package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 推广费（链接级/店铺级，按月）
 * @TableName erp_promotion_fee
 */
@TableName(value = "erp_promotion_fee")
@Data
public class ErpPromotionFee implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 链接/商品ID（空=店铺级统一分摊）
     */
    private Long goodsId;

    /**
     * 链接名称（冗余）
     */
    private String goodsName;

    /**
     * 推广计划名（直通车/千川/多多搜索/其他）
     */
    private String planName;

    /**
     * 费用月份 yyyy-MM
     */
    private String feeMonth;

    /**
     * 金额
     */
    private BigDecimal amount;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
