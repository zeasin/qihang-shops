package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpGoodsSku;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.domain.ErpOrderItem;
import cn.qihangerp.api.domain.vo.SalesDailyVo;
import cn.qihangerp.api.mapper.ErpOrderItemMapper;
import cn.qihangerp.api.mapper.ErpOrderMapper;
import cn.qihangerp.api.request.OrderSearchRequest;
import cn.qihangerp.api.service.ErpGoodsSkuService;
import cn.qihangerp.api.service.ErpOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author TW
* @description 针对表【erp_order(订单表)】的数据库操作Service实现
* @createDate 2024-04-03 15:57:41
*/
@AllArgsConstructor
@Service
public class ErpOrderServiceImpl extends ServiceImpl<ErpOrderMapper, ErpOrder>
    implements ErpOrderService{
    private final ErpOrderMapper mapper;
    private final ErpOrderItemMapper orderItemMapper;
    private final ErpGoodsSkuService goodsSkuService;

    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);

    @Override
    public PageResult<ErpOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery) {
        if(org.springframework.util.StringUtils.hasText(bo.getStartTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getStartTime());
            boolean b = matcher.find();
            if(b){
                bo.setStartTime(bo.getStartTime()+" 00:00:00");
            }
        }
        if(org.springframework.util.StringUtils.hasText(bo.getEndTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getEndTime());
            boolean b = matcher.find();
            if(b){
                bo.setEndTime(bo.getEndTime()+" 23:59:59");
            }
        }

        LambdaQueryWrapper<ErpOrder> queryWrapper = new LambdaQueryWrapper<ErpOrder>()
                .eq(bo.getShopId()!=null,ErpOrder::getShopId,bo.getShopId())
                .eq(bo.getOrderStatus()!=null,ErpOrder::getOrderStatus,bo.getOrderStatus())
                .eq(bo.getRefundStatus()!=null,ErpOrder::getRefundStatus,bo.getRefundStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),ErpOrder::getOrderTime,bo.getStartTime())
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),ErpOrder::getOrderTime,bo.getEndTime())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),ErpOrder::getOrderNum,bo.getOrderNum())
                ;
        Page<ErpOrder> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        if(pages.getRecords()!=null){
            for (var order:pages.getRecords()) {
                order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<ErpOrderItem>().eq(ErpOrderItem::getOrderId, order.getId())));
            }
        }

        return PageResult.build(pages);
    }

    @Override
    public ErpOrder queryDetailById(Long id) {
        ErpOrder order = mapper.selectById(id);
        if(order!=null){
            order.setItemList(orderItemMapper.selectList(new LambdaQueryWrapper<ErpOrderItem>().eq(ErpOrderItem::getOrderId, order.getId())));
        }
        return order;
    }

    @Override
    public List<SalesDailyVo> salesDaily() {
        return mapper.salesDaily();
    }
    @Override
    public SalesDailyVo getTodaySalesDaily() {
        return mapper.getTodaySalesDaily();
    }

    @Override
    public Integer getWaitShipOrderAllCount() {
        return mapper.getWaitShipOrderAllCount();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int shipOrders(String userName, List<String> ids, Double expressFee, String waybillCode) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("请选择要发货的订单");
        }
        if (expressFee == null || expressFee < 0) {
            throw new IllegalArgumentException("请填写发货成本（快递费）");
        }
        String waybill = waybillCode == null ? null : waybillCode.trim();
        if (waybill != null && waybill.isEmpty()) {
            waybill = null;
        }
        if (waybill != null && waybill.length() > 55) {
            throw new IllegalArgumentException("快递单号不能超过55个字符");
        }
        List<ErpOrder> orders = mapper.selectBatchIds(ids);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("订单不存在");
        }
        for (ErpOrder order : orders) {
            if (!Integer.valueOf(1).equals(order.getOrderStatus())) {
                throw new IllegalArgumentException("订单 " + order.getOrderNum() + " 不是待发货状态");
            }
        }

        LocalDateTime shipTime = LocalDateTime.now();
        Date now = new Date();
        for (ErpOrder order : orders) {
            List<ErpOrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<ErpOrderItem>().eq(ErpOrderItem::getOrderId, order.getId()));
            for (ErpOrderItem item : items) {
                if (item.getErpSkuId() != null) {
                    ErpGoodsSku sku = goodsSkuService.getById(item.getErpSkuId());
                    if (sku != null) {
                        item.setUnitCost(sku.getUnitCost());
                    }
                }
                item.setShipStatus(2);
                item.setUpdateTime(now);
                item.setUpdateBy(userName);
                orderItemMapper.updateById(item);
            }

            order.setExpressFee(expressFee);
            if (waybill != null) {
                order.setWaybillCode(waybill);
                order.setWaybillTime(now);
            }
            order.setOrderStatus(2);
            order.setShipStatus(2);
            order.setShipType(0);
            order.setShipper(0);
            order.setShipTime(shipTime);
            order.setUpdateTime(now);
            order.setUpdateBy(userName);
            mapper.updateById(order);
        }
        return orders.size();
    }
}
