package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺固定费用（人工/房租/软件/包装/其他，按月）
 * @TableName erp_shop_fixed_fee
 */
@TableName(value = "erp_shop_fixed_fee")
@Data
public class ErpShopFixedFee implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 费用月份 yyyy-MM
     */
    private String feeMonth;

    /**
     * 费用项 人工/房租/软件/包装/其他
     */
    private String feeType;

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
