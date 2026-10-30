package cn.qihangerp.api.request;

import lombok.Data;

/**
 * 官网转发的微信公众号扫码事件
 */
@Data
public class MpScanNotify {
    /** 扫码场景ID（erp_ 前缀） */
    private String sceneId;
    /** 用户微信 openId */
    private String openId;
    /** 事件类型：subscribe / SCAN */
    private String event;
}
