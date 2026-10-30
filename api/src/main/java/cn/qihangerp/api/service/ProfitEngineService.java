package cn.qihangerp.api.service;

import cn.qihangerp.api.domain.vo.CompletenessVo;
import cn.qihangerp.api.domain.vo.LinkProfitVo;
import cn.qihangerp.api.domain.vo.OrderProfitVo;
import cn.qihangerp.api.domain.vo.ShopProfitVo;

import java.util.List;

/**
 * 利润核算引擎
 *
 * 三个口径（退款不会扣两次：平台退的钱已在收入里减掉，工单只记额外损失）：
 *  订单贡献利润 = 实收 − 商品成本 − 快递 − 平台扣费 − 售后成本(已结算工单)
 *  链接贡献利润 = 订单贡献利润汇总 − 分摊推广费（按实收占比）
 *  店铺净利润   = 链接贡献利润汇总 − 固定费用
 */
public interface ProfitEngineService {

    /**
     * 订单级贡献利润（一行一单）
     *
     * @param month    月份 yyyy-MM
     * @param shopId   店铺ID（可空）
     */
    List<OrderProfitVo> orderProfit(String month, Long shopId);

    /**
     * 链接级贡献利润（一行一款，推广费为分摊后）
     */
    List<LinkProfitVo> linkProfit(String month, Long shopId);

    /**
     * 店铺级净利润（一行一店，含固定费用）
     */
    List<ShopProfitVo> shopProfit(String month, Long shopId);

    /**
     * 数据完整度检查
     */
    CompletenessVo completeness(String month);
}
