package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺平台（电商平台基础配置）
 *
 * @TableName erp_shop_platform
 */
@TableName(value = "erp_shop_platform")
@Data
public class ErpShopPlatform implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台名称，如 拼多多 / 抖店 / 微信小店
     */
    private String name;

    /**
     * 平台编码，如 PDD / DOU / WEI
     */
    private String code;

    /**
     * 平台接口 appKey
     */
    private String appKey;

    /**
     * 平台接口 appSecret
     */
    private String appSecret;

    /**
     * 授权回调地址
     */
    private String redirectUri;

    /**
     * 平台接口请求地址
     */
    private String serverUrl;

    private static final long serialVersionUID = 1L;
}
