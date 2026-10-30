package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 平台对账单明细（平台扣费/退款的数据来源）
 * @TableName erp_platform_bill
 */
@TableName(value = "erp_platform_bill")
@Data
public class ErpPlatformBill implements Serializable {

    /** 收支：收入 */
    public static final int INCOME_TYPE_IN = 1;
    /** 收支：支出 */
    public static final int INCOME_TYPE_OUT = 2;

    /** 匹配状态：未匹配 */
    public static final int MATCH_UNMATCHED = 0;
    /** 匹配状态：已匹配 */
    public static final int MATCH_MATCHED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 账期月份 yyyy-MM
     */
    private String billMonth;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 业务类型 佣金/推广扣费/运费/退款/活动补贴/其他
     */
    private String bizType;

    /**
     * 收支 1收入 2支出
     */
    private Integer incomeType;

    /**
     * 金额（正数）
     */
    private BigDecimal amount;

    /**
     * 发生时间
     */
    private Date bizTime;

    /**
     * 匹配状态 0未匹配 1已匹配
     */
    private Integer matchStatus;

    /**
     * 匹配的系统订单ID
     */
    private String orderId;

    /**
     * 上传批次号
     */
    private String batchNo;

    private String remark;

    private String createBy;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
