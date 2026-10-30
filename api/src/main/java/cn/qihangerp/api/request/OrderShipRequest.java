package cn.qihangerp.api.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发货请求
 */
@Data
public class OrderShipRequest implements Serializable {

    /**
     * 订单ID集合
     */
    private List<String> ids;

    /**
     * 发货成本（快递费）
     */
    private Double expressFee;

    /**
     * 快递单号（选填）
     */
    private String waybillCode;

    private static final long serialVersionUID = 1L;
}
