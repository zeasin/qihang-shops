package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.domain.*;
import cn.qihangerp.api.domain.vo.CompletenessVo;
import cn.qihangerp.api.domain.vo.LinkProfitVo;
import cn.qihangerp.api.domain.vo.OrderProfitVo;
import cn.qihangerp.api.domain.vo.ShopProfitVo;
import cn.qihangerp.api.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 利润核算引擎实现
 *
 * 数据流：订单+明细 → 订单利润 → 按商品金额占比分摊到链接 → 店铺汇总扣固定费用。
 * 平台扣费优先取对账单匹配净额（支出−收入），无匹配时回退订单自带 platform_fee。
 * 售后成本只计已结算工单；无订单号的已结算工单按结算月归入店铺层。
 */
@Slf4j
@AllArgsConstructor
@Service
public class ProfitEngineServiceImpl implements ProfitEngineService {

    private final ErpOrderService orderService;
    private final ErpOrderItemService orderItemService;
    private final ErpGoodsSkuService goodsSkuService;
    private final ErpWorkOrderService workOrderService;
    private final ErpPromotionFeeService promotionFeeService;
    private final ErpShopFixedFeeService fixedFeeService;
    private final ErpPlatformBillService platformBillService;
    private final ShopService shopService;

    private static final int IN_CHUNK = 500;

    @Override
    public List<OrderProfitVo> orderProfit(String month, Long shopId) {
        ProfitData data = loadData(month, shopId);
        List<OrderProfitVo> rows = new ArrayList<>();
        for (ErpOrder order : data.orders) {
            List<ErpOrderItem> items = data.itemsByOrder.getOrDefault(order.getId(), Collections.emptyList());
            BigDecimal payAmount = money(order.getPayAmount(), order.getOrderAmount());
            BigDecimal expressFee = nvl(order.getExpressFee());
            BigDecimal afterSaleCost = data.afterSaleByOrderNum.getOrDefault(order.getOrderNum(), BigDecimal.ZERO);

            BigDecimal platformFee;
            String platformFeeSource;
            if (data.platformFeeByOrderNum.containsKey(order.getOrderNum())) {
                platformFee = data.platformFeeByOrderNum.get(order.getOrderNum());
                platformFeeSource = "bill";
            } else {
                platformFee = nvl(order.getPlatformFee());
                platformFeeSource = "order";
            }

            BigDecimal goodsCost = BigDecimal.ZERO;
            for (ErpOrderItem item : items) {
                BigDecimal unitCost = effectiveUnitCost(item, data);
                if (unitCost != null) {
                    goodsCost = goodsCost.add(unitCost.multiply(BigDecimal.valueOf(safeQty(item))));
                }
            }

            OrderProfitVo vo = new OrderProfitVo();
            vo.setOrderId(order.getId());
            vo.setOrderNum(order.getOrderNum());
            vo.setShopId(order.getShopId());
            vo.setShopName(data.shopNames.get(order.getShopId()));
            vo.setOrderTime(order.getOrderTime() == null ? null : order.getOrderTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            vo.setPayAmount(round2(payAmount));
            vo.setGoodsCost(round2(goodsCost));
            vo.setExpressFee(round2(expressFee));
            vo.setPlatformFee(round2(platformFee));
            vo.setPlatformFeeSource(platformFeeSource);
            vo.setAfterSaleCost(round2(afterSaleCost));
            BigDecimal profit = payAmount.subtract(goodsCost).subtract(expressFee).subtract(platformFee).subtract(afterSaleCost);
            vo.setProfit(round2(profit));
            vo.setProfitRate(rate(profit, payAmount));
            rows.add(vo);
        }
        return rows;
    }

    @Override
    public List<LinkProfitVo> linkProfit(String month, Long shopId) {
        ProfitData data = loadData(month, shopId);
        Map<String, LinkAgg> linkAggs = aggregateLinks(data);

        // 推广费分摊：链接级直归 + 店铺级按链接实收占比
        Map<Long, BigDecimal> directPromo = new HashMap<>();
        Map<Long, BigDecimal> shopPoolPromo = new HashMap<>();
        for (ErpPromotionFee fee : data.promotionFees) {
            if (fee.getGoodsId() != null) {
                directPromo.merge(fee.getGoodsId(), fee.getAmount(), BigDecimal::add);
            } else {
                shopPoolPromo.merge(fee.getShopId(), fee.getAmount(), BigDecimal::add);
            }
        }
        Map<Long, BigDecimal> shopPayTotal = new HashMap<>();
        for (LinkAgg agg : linkAggs.values()) {
            shopPayTotal.merge(agg.shopId, agg.payAmount, BigDecimal::add);
        }

        List<LinkProfitVo> rows = new ArrayList<>();
        for (LinkAgg agg : linkAggs.values()) {
            BigDecimal shopFee = BigDecimal.ZERO;
            BigDecimal pool = shopPoolPromo.getOrDefault(agg.shopId, BigDecimal.ZERO);
            BigDecimal shopPay = shopPayTotal.getOrDefault(agg.shopId, BigDecimal.ZERO);
            if (pool.signum() > 0 && shopPay.signum() > 0 && agg.payAmount.signum() > 0) {
                shopFee = pool.multiply(agg.payAmount).divide(shopPay, 6, RoundingMode.HALF_UP);
            }
            BigDecimal promotionFee = directPromo.getOrDefault(agg.goodsId, BigDecimal.ZERO).add(shopFee);
            BigDecimal profit = agg.payAmount.subtract(agg.goodsCost).subtract(agg.expressFee)
                    .subtract(agg.platformFee).subtract(agg.afterSaleCost).subtract(promotionFee);

            LinkProfitVo vo = new LinkProfitVo();
            vo.setGoodsId(agg.goodsId);
            vo.setGoodsName(agg.goodsName);
            vo.setSalesQty(agg.salesQty);
            vo.setPayAmount(round2(agg.payAmount));
            vo.setGoodsCost(round2(agg.goodsCost));
            vo.setExpressFee(round2(agg.expressFee));
            vo.setPlatformFee(round2(agg.platformFee));
            vo.setAfterSaleCost(round2(agg.afterSaleCost));
            vo.setPromotionFee(round2(promotionFee));
            vo.setProfit(round2(profit));
            vo.setProfitRate(rate(profit, agg.payAmount));
            rows.add(vo);
        }
        rows.sort(Comparator.comparing(LinkProfitVo::getPayAmount, Comparator.nullsLast(Comparator.reverseOrder())));
        return rows;
    }

    @Override
    public List<ShopProfitVo> shopProfit(String month, Long shopId) {
        ProfitData data = loadData(month, shopId);
        Map<String, LinkAgg> linkAggs = aggregateLinks(data);

        // 店铺层售后成本补充：无订单号的已结算工单（按结算月归属）
        Map<Long, BigDecimal> extraAfterSale = new HashMap<>();
        for (ErpWorkOrder wo : data.unmatchedSettledWorkOrders) {
            extraAfterSale.merge(wo.getShopId() == null ? 0L : wo.getShopId(), wo.getAmount(), BigDecimal::add);
        }

        Map<Long, ShopProfitVo> byShop = new LinkedHashMap<>();
        for (LinkAgg agg : linkAggs.values()) {
            ShopProfitVo vo = byShop.computeIfAbsent(agg.shopId, k -> {
                ShopProfitVo v = new ShopProfitVo();
                v.setShopId(k);
                v.setShopName(data.shopNames.getOrDefault(k, k == 0 ? "未知店铺" : "店铺" + k));
                v.setPayAmount(BigDecimal.ZERO);
                v.setGoodsCost(BigDecimal.ZERO);
                v.setExpressFee(BigDecimal.ZERO);
                v.setPlatformFee(BigDecimal.ZERO);
                v.setAfterSaleCost(BigDecimal.ZERO);
                v.setPromotionFee(BigDecimal.ZERO);
                v.setFixedFee(BigDecimal.ZERO);
                v.setNetProfit(BigDecimal.ZERO);
                return v;
            });
            vo.setPayAmount(vo.getPayAmount().add(agg.payAmount));
            vo.setGoodsCost(vo.getGoodsCost().add(agg.goodsCost));
            vo.setExpressFee(vo.getExpressFee().add(agg.expressFee));
            vo.setPlatformFee(vo.getPlatformFee().add(agg.platformFee));
            vo.setAfterSaleCost(vo.getAfterSaleCost().add(agg.afterSaleCost));
            vo.setNetProfit(vo.getNetProfit().add(agg.payAmount.subtract(agg.goodsCost).subtract(agg.expressFee)
                    .subtract(agg.platformFee).subtract(agg.afterSaleCost)));
        }
        // 推广费合计（链接级+店铺级）
        for (ErpPromotionFee fee : data.promotionFees) {
            ShopProfitVo vo = byShop.computeIfAbsent(fee.getShopId(), k -> newShopRow(k, data));
            vo.setPromotionFee(vo.getPromotionFee().add(fee.getAmount()));
            vo.setNetProfit(vo.getNetProfit().subtract(fee.getAmount()));
        }
        // 无订单号的售后支出 + 固定费用
        for (Map.Entry<Long, BigDecimal> e : extraAfterSale.entrySet()) {
            ShopProfitVo vo = byShop.computeIfAbsent(e.getKey(), k -> newShopRow(k, data));
            vo.setAfterSaleCost(vo.getAfterSaleCost().add(e.getValue()));
            vo.setNetProfit(vo.getNetProfit().subtract(e.getValue()));
        }
        for (ErpShopFixedFee fee : data.fixedFees) {
            ShopProfitVo vo = byShop.computeIfAbsent(fee.getShopId(), k -> newShopRow(k, data));
            vo.setFixedFee(vo.getFixedFee().add(fee.getAmount()));
            vo.setNetProfit(vo.getNetProfit().subtract(fee.getAmount()));
        }

        List<ShopProfitVo> rows = new ArrayList<>(byShop.values());
        for (ShopProfitVo vo : rows) {
            vo.setPayAmount(round2(vo.getPayAmount()));
            vo.setGoodsCost(round2(vo.getGoodsCost()));
            vo.setExpressFee(round2(vo.getExpressFee()));
            vo.setPlatformFee(round2(vo.getPlatformFee()));
            vo.setAfterSaleCost(round2(vo.getAfterSaleCost()));
            vo.setPromotionFee(round2(vo.getPromotionFee()));
            vo.setFixedFee(round2(vo.getFixedFee()));
            vo.setNetProfit(round2(vo.getNetProfit()));
            vo.setProfitRate(rate(vo.getNetProfit(), vo.getPayAmount()));
        }
        rows.sort(Comparator.comparing(ShopProfitVo::getPayAmount, Comparator.nullsLast(Comparator.reverseOrder())));
        return rows;
    }

    @Override
    public CompletenessVo completeness(String month) {
        ProfitData data = loadData(month, null);

        CompletenessVo vo = new CompletenessVo();
        vo.setMonth(month);
        vo.setOrderCount((long) data.orders.size());
        vo.setUnmatchedCostItemCount(data.unmatchedCostItemCount);
        vo.setUnmatchedCostOrderCount((long) data.unmatchedCostOrderNums.size());
        vo.setPromotionFeeEntered(!data.promotionFees.isEmpty());
        vo.setFixedFeeEntered(!data.fixedFees.isEmpty());

        Long unmatchedBills = platformBillService.count(new LambdaQueryWrapper<ErpPlatformBill>()
                
                .eq(ErpPlatformBill::getBillMonth, month)
                .eq(ErpPlatformBill::getMatchStatus, ErpPlatformBill.MATCH_UNMATCHED));
        vo.setUnmatchedBillCount(unmatchedBills);

        Long pendingWorkOrders = workOrderService.count(new LambdaQueryWrapper<ErpWorkOrder>()
                
                .eq(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_PENDING));
        vo.setPendingWorkOrderCount(pendingWorkOrders);

        List<String> messages = new ArrayList<>();
        if (data.orders.isEmpty()) {
            messages.add("本月暂无订单数据，请先在「订单-订单导入」导入订单");
        }
        if (data.unmatchedCostItemCount > 0) {
            messages.add(data.unmatchedCostItemCount + " 个订单明细未匹配商品成本，请到「商品成本-商品档案」补录成本价");
        }
        if (!vo.getPromotionFeeEntered()) {
            messages.add("本月推广费未录入，链接利润未扣除推广费");
        }
        if (!vo.getFixedFeeEntered()) {
            messages.add("本月固定费用未录入，店铺净利润未扣除人工房租等固定支出");
        }
        if (unmatchedBills > 0) {
            messages.add(unmatchedBills + " 笔账单明细未匹配订单，请核对订单号");
        }
        if (pendingWorkOrders > 0) {
            messages.add(pendingWorkOrders + " 笔工单待处理，结算后才计入利润");
        }
        vo.setMessages(messages);
        vo.setComplete(messages.isEmpty());
        return vo;
    }

    // ==================== 内部结构 ====================

    private ShopProfitVo newShopRow(Long shopId, ProfitData data) {
        ShopProfitVo v = new ShopProfitVo();
        v.setShopId(shopId);
        v.setShopName(data.shopNames.getOrDefault(shopId, shopId == null || shopId == 0 ? "未知店铺" : "店铺" + shopId));
        v.setPayAmount(BigDecimal.ZERO);
        v.setGoodsCost(BigDecimal.ZERO);
        v.setExpressFee(BigDecimal.ZERO);
        v.setPlatformFee(BigDecimal.ZERO);
        v.setAfterSaleCost(BigDecimal.ZERO);
        v.setPromotionFee(BigDecimal.ZERO);
        v.setFixedFee(BigDecimal.ZERO);
        v.setNetProfit(BigDecimal.ZERO);
        return v;
    }

    private static class LinkAgg {
        Long goodsId;
        String goodsName;
        Long shopId;
        int salesQty;
        BigDecimal payAmount = BigDecimal.ZERO;
        BigDecimal goodsCost = BigDecimal.ZERO;
        BigDecimal expressFee = BigDecimal.ZERO;
        BigDecimal platformFee = BigDecimal.ZERO;
        BigDecimal afterSaleCost = BigDecimal.ZERO;
    }

    /**
     * 把订单层金额按商品金额占比分摊到链接
     */
    private Map<String, LinkAgg> aggregateLinks(ProfitData data) {
        Map<String, LinkAgg> linkAggs = new LinkedHashMap<>();
        for (ErpOrder order : data.orders) {
            List<ErpOrderItem> items = data.itemsByOrder.getOrDefault(order.getId(), Collections.emptyList());
            BigDecimal payAmount = money(order.getPayAmount(), order.getOrderAmount());
            BigDecimal expressFee = nvl(order.getExpressFee());
            BigDecimal afterSaleCost = data.afterSaleByOrderNum.getOrDefault(order.getOrderNum(), BigDecimal.ZERO);
            BigDecimal platformFee = data.platformFeeByOrderNum.containsKey(order.getOrderNum())
                    ? data.platformFeeByOrderNum.get(order.getOrderNum())
                    : nvl(order.getPlatformFee());

            // 分摊权重：按明细金额占比；无金额明细时平分
            BigDecimal totalItemAmount = BigDecimal.ZERO;
            int validItems = 0;
            for (ErpOrderItem item : items) {
                BigDecimal amt = itemAmount(item);
                totalItemAmount = totalItemAmount.add(amt);
                validItems++;
            }
            // 无明细的订单挂到"未知商品"，避免金额丢失
            if (items.isEmpty()) {
                LinkAgg agg = linkAggs.computeIfAbsent("0|未知商品", k -> newAgg(k));
                agg.payAmount = agg.payAmount.add(payAmount);
                agg.expressFee = agg.expressFee.add(expressFee);
                agg.platformFee = agg.platformFee.add(platformFee);
                agg.afterSaleCost = agg.afterSaleCost.add(afterSaleCost);
                continue;
            }

            for (ErpOrderItem item : items) {
                BigDecimal amt = itemAmount(item);
                BigDecimal share = amt.signum() > 0 && totalItemAmount.signum() > 0
                        ? amt.divide(totalItemAmount, 6, RoundingMode.HALF_UP)
                        : BigDecimal.ONE.divide(BigDecimal.valueOf(validItems), 6, RoundingMode.HALF_UP);

                Long goodsId = item.getErpGoodsId() == null ? 0L : item.getErpGoodsId();
                String name = StringUtils.hasText(item.getGoodsTitle()) ? item.getGoodsTitle() : "未知商品";
                LinkAgg agg = linkAggs.computeIfAbsent(goodsId + "|" + name, k -> newAgg(k));
                agg.goodsId = goodsId;
                agg.goodsName = name;
                agg.shopId = order.getShopId() == null ? 0L : order.getShopId();
                agg.salesQty += safeQty(item);
                agg.payAmount = agg.payAmount.add(payAmount.multiply(share));
                agg.expressFee = agg.expressFee.add(expressFee.multiply(share));
                agg.platformFee = agg.platformFee.add(platformFee.multiply(share));
                agg.afterSaleCost = agg.afterSaleCost.add(afterSaleCost.multiply(share));

                BigDecimal unitCost = effectiveUnitCost(item, data);
                if (unitCost != null) {
                    agg.goodsCost = agg.goodsCost.add(unitCost.multiply(BigDecimal.valueOf(safeQty(item))));
                }
            }
        }
        return linkAggs;
    }

    private LinkAgg newAgg(String key) {
        LinkAgg agg = new LinkAgg();
        agg.shopId = 0L;
        return agg;
    }

    /**
     * 一次性装载利润核算所需数据
     */
    private ProfitData loadData(String month, Long shopId) {
        ProfitData data = new ProfitData();
        YearMonth ym = parseMonth(month);
        LocalDateTime begin = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        Date beginDate = Date.from(begin.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        // 1. 订单
        data.orders = orderService.list(new LambdaQueryWrapper<ErpOrder>()
                
                .eq(shopId != null, ErpOrder::getShopId, shopId)
                .isNotNull(ErpOrder::getOrderTime)
                .ge(ErpOrder::getOrderTime, begin)
                .le(ErpOrder::getOrderTime, end)
                .orderByDesc(ErpOrder::getOrderTime));

        // 2. 订单明细（分批 IN 查询）
        data.itemsByOrder = new HashMap<>();
        data.skuCost = new HashMap<>();
        data.unmatchedCostItemCount = 0L;
        data.unmatchedCostOrderNums = new HashSet<>();
        List<String> orderIds = data.orders.stream().map(ErpOrder::getId).collect(Collectors.toList());
        List<ErpOrderItem> allItems = new ArrayList<>();
        for (int i = 0; i < orderIds.size(); i += IN_CHUNK) {
            allItems.addAll(orderItemService.list(new LambdaQueryWrapper<ErpOrderItem>()
                    
                    .in(ErpOrderItem::getOrderId, orderIds.subList(i, Math.min(i + IN_CHUNK, orderIds.size())))));
        }
        Set<Long> skuIds = new HashSet<>();
        for (ErpOrderItem item : allItems) {
            data.itemsByOrder.computeIfAbsent(item.getOrderId(), k -> new ArrayList<>()).add(item);
            if (item.getErpSkuId() != null) {
                skuIds.add(item.getErpSkuId());
            }
        }
        if (!skuIds.isEmpty()) {
            List<Long> skuIdList = new ArrayList<>(skuIds);
            for (int i = 0; i < skuIdList.size(); i += IN_CHUNK) {
                for (ErpGoodsSku sku : goodsSkuService.listByIds(skuIdList.subList(i, Math.min(i + IN_CHUNK, skuIdList.size())))) {
                    if (sku.getUnitCost() != null) {
                        data.skuCost.put(sku.getId(), sku.getUnitCost());
                    }
                }
            }
        }
        for (ErpOrderItem item : allItems) {
            boolean unmatched = effectiveUnitCost(item, data) == null;
            if (unmatched) {
                data.unmatchedCostItemCount++;
                data.unmatchedCostOrderNums.add(item.getOrderNum());
            }
        }

        // 3. 已结算工单：有单号的按订单号归集；无单号的按结算月归店铺层
        List<ErpWorkOrder> settled = workOrderService.list(new LambdaQueryWrapper<ErpWorkOrder>()
                
                .eq(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_SETTLED));
        Set<String> monthOrderNums = data.orders.stream()
                .map(ErpOrder::getOrderNum).filter(StringUtils::hasText).collect(Collectors.toSet());
        data.afterSaleByOrderNum = new HashMap<>();
        data.unmatchedSettledWorkOrders = new ArrayList<>();
        for (ErpWorkOrder wo : settled) {
            if (StringUtils.hasText(wo.getOrderNum()) && monthOrderNums.contains(wo.getOrderNum())) {
                data.afterSaleByOrderNum.merge(wo.getOrderNum(), wo.getAmount(), BigDecimal::add);
            } else if (wo.getSettleTime() != null
                    && !wo.getSettleTime().before(beginDate) && !wo.getSettleTime().after(endDate)
                    && (shopId == null || shopId.equals(wo.getShopId()))) {
                data.unmatchedSettledWorkOrders.add(wo);
            }
        }

        // 4. 平台账单（已匹配）：订单号 → 净扣费（支出−收入）
        List<ErpPlatformBill> bills = platformBillService.list(new LambdaQueryWrapper<ErpPlatformBill>()
                
                .eq(ErpPlatformBill::getBillMonth, month)
                .eq(shopId != null, ErpPlatformBill::getShopId, shopId)
                .eq(ErpPlatformBill::getMatchStatus, ErpPlatformBill.MATCH_MATCHED));
        data.platformFeeByOrderNum = new HashMap<>();
        for (ErpPlatformBill bill : bills) {
            if (!StringUtils.hasText(bill.getOrderNum())) {
                continue;
            }
            BigDecimal net = bill.getIncomeType() != null && bill.getIncomeType() == ErpPlatformBill.INCOME_TYPE_IN
                    ? bill.getAmount().negate() : bill.getAmount();
            data.platformFeeByOrderNum.merge(bill.getOrderNum(), net, BigDecimal::add);
        }

        // 5. 推广费 / 固定费用
        data.promotionFees = promotionFeeService.list(new LambdaQueryWrapper<ErpPromotionFee>()
                
                .eq(ErpPromotionFee::getFeeMonth, month)
                .eq(shopId != null, ErpPromotionFee::getShopId, shopId));
        data.fixedFees = fixedFeeService.list(new LambdaQueryWrapper<ErpShopFixedFee>()
                
                .eq(ErpShopFixedFee::getFeeMonth, month)
                .eq(shopId != null, ErpShopFixedFee::getShopId, shopId));

        // 6. 店铺名
        Set<Long> shopIdSet = new HashSet<>();
        data.orders.forEach(o -> shopIdSet.add(o.getShopId()));
        data.promotionFees.forEach(f -> shopIdSet.add(f.getShopId()));
        data.fixedFees.forEach(f -> shopIdSet.add(f.getShopId()));
        data.unmatchedSettledWorkOrders.forEach(w -> shopIdSet.add(w.getShopId()));
        shopIdSet.remove(null);
        data.shopNames = new HashMap<>();
        if (!shopIdSet.isEmpty()) {
            for (Shop shop : shopService.listByIds(new ArrayList<>(shopIdSet))) {
                try {
                    data.shopNames.put(Long.valueOf(shop.getId()), shop.getName());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return data;
    }

    private static class ProfitData {
        List<ErpOrder> orders;
        Map<String, List<ErpOrderItem>> itemsByOrder;
        Map<Long, BigDecimal> skuCost;
        Map<String, BigDecimal> afterSaleByOrderNum;
        Map<String, BigDecimal> platformFeeByOrderNum;
        List<ErpWorkOrder> unmatchedSettledWorkOrders;
        List<ErpPromotionFee> promotionFees;
        List<ErpShopFixedFee> fixedFees;
        Map<Long, String> shopNames;
        long unmatchedCostItemCount;
        Set<String> unmatchedCostOrderNums;
    }

    // ==================== 工具 ====================

    private static YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month.trim());
        } catch (Exception e) {
            return YearMonth.now();
        }
    }

    private static BigDecimal money(Double v, Double fallback) {
        if (v != null) {
            return BigDecimal.valueOf(v);
        }
        return fallback != null ? BigDecimal.valueOf(fallback) : BigDecimal.ZERO;
    }

    private static BigDecimal nvl(Double v) {
        return v != null ? BigDecimal.valueOf(v) : BigDecimal.ZERO;
    }

    private static int safeQty(ErpOrderItem item) {
        return item.getQuantity() == null || item.getQuantity() < 1 ? 1 : item.getQuantity();
    }

    /**
     * 发货时写入的成本快照优先；历史/导入订单未快照时回退当前商品成本档案
     */
    private static BigDecimal effectiveUnitCost(ErpOrderItem item, ProfitData data) {
        if (item.getUnitCost() != null) {
            return item.getUnitCost();
        }
        return item.getErpSkuId() == null ? null : data.skuCost.get(item.getErpSkuId());
    }

    private static BigDecimal itemAmount(ErpOrderItem item) {
        if (item.getItemAmount() != null && item.getItemAmount() > 0) {
            return BigDecimal.valueOf(item.getItemAmount());
        }
        if (item.getGoodsPrice() != null && item.getGoodsPrice() > 0) {
            return BigDecimal.valueOf(item.getGoodsPrice()).multiply(BigDecimal.valueOf(safeQty(item)));
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal round2(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal rate(BigDecimal profit, BigDecimal revenue) {
        if (revenue == null || revenue.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return profit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
    }
}
