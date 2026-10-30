package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.common.ResultVo;
import cn.qihangerp.api.common.ResultVoEnum;
import cn.qihangerp.api.domain.ErpGoodsInventory;
import cn.qihangerp.api.domain.ErpGoodsInventoryBatch;
import cn.qihangerp.api.domain.ErpGoodsInventoryOperation;
import cn.qihangerp.api.domain.ErpGoodsSku;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.domain.ErpWorkOrder;
import cn.qihangerp.api.mapper.ErpGoodsInventoryOperationMapper;
import cn.qihangerp.api.mapper.ErpOrderMapper;
import cn.qihangerp.api.mapper.ErpWorkOrderMapper;
import cn.qihangerp.api.request.GoodsSkuInventoryVo;
import cn.qihangerp.api.request.StockOutCreateRequest;
import cn.qihangerp.api.service.ErpGoodsInventoryBatchService;
import cn.qihangerp.api.service.ErpGoodsInventoryService;
import cn.qihangerp.api.service.ErpGoodsSkuService;
import cn.qihangerp.api.service.ErpWorkOrderService;
import cn.qihangerp.api.service.WmsStockOutService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 工单（售后支出记账）服务实现
 */
@Slf4j
@AllArgsConstructor
@Service
public class ErpWorkOrderServiceImpl extends ServiceImpl<ErpWorkOrderMapper, ErpWorkOrder>
        implements ErpWorkOrderService {

    private final WmsStockOutService stockOutService;
    private final ErpGoodsSkuService goodsSkuService;
    private final ErpOrderMapper orderMapper;
    private final ErpGoodsInventoryService goodsInventoryService;
    private final ErpGoodsInventoryBatchService goodsInventoryBatchService;
    private final ErpGoodsInventoryOperationMapper goodsInventoryOperationMapper;

    @Override
    public PageResult<ErpWorkOrder> queryPageList(ErpWorkOrder bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(bo.getShopId() != null, ErpWorkOrder::getShopId, bo.getShopId());
        queryWrapper.eq(bo.getType() != null, ErpWorkOrder::getType, bo.getType());
        queryWrapper.eq(bo.getStatus() != null, ErpWorkOrder::getStatus, bo.getStatus());
        queryWrapper.like(StringUtils.hasText(bo.getOrderNum()), ErpWorkOrder::getOrderNum, bo.getOrderNum());
        // 按快递单号查询：先反查订单号，再匹配工单
        if (StringUtils.hasText(bo.getWaybillCode())) {
            List<String> orderNums = orderMapper.selectList(new LambdaQueryWrapper<ErpOrder>()
                            .like(ErpOrder::getWaybillCode, bo.getWaybillCode().trim()))
                    .stream()
                    .map(ErpOrder::getOrderNum)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .toList();
            if (orderNums.isEmpty()) {
                queryWrapper.apply("1 = 0");
            } else {
                queryWrapper.in(ErpWorkOrder::getOrderNum, orderNums);
            }
        }
        // 待处理置顶，再按创建时间倒序
        queryWrapper.orderByAsc(ErpWorkOrder::getStatus);
        queryWrapper.orderByDesc(ErpWorkOrder::getId);
        Page<ErpWorkOrder> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public ResultVo<Long> createWorkOrder(Long operatorId, String userName, ErpWorkOrder workOrder) {
        if (workOrder.getType() == null) {
            return ResultVo.error(ResultVoEnum.ParamsError, "缺少工单类型");
        }
        if (workOrder.getAmount() == null || workOrder.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return ResultVo.error(ResultVoEnum.ParamsError, "金额不能为空且不能为负数");
        }
        workOrder.setCreateBy(userName);
        workOrder.setCreateTime(new Date());
        workOrder.setStatus(ErpWorkOrder.STATUS_PENDING);
        if (workOrder.getOrderNum() != null) {
            workOrder.setOrderNum(workOrder.getOrderNum().trim());
        }
        this.save(workOrder);

        // 补发工单联动生成出库单并实时扣减库存
        if (workOrder.getType() == ErpWorkOrder.TYPE_RESEND && workOrder.getGoodsId() != null) {
            createResendStockOut(operatorId, userName, workOrder);
        }
        return ResultVo.success(workOrder.getId());
    }

    private void createResendStockOut(Long operatorId, String userName, ErpWorkOrder workOrder) {
        try {
            Long skuId = workOrder.getSkuId();
            ErpGoodsSku sku;
            if (skuId == null) {
                // 未指定规格时取该商品默认第一个规格
                List<ErpGoodsSku> skus = goodsSkuService.list(new LambdaQueryWrapper<ErpGoodsSku>()
                        .eq(ErpGoodsSku::getGoodsId, workOrder.getGoodsId())
                        .last("limit 1"));
                if (skus.isEmpty()) {
                    log.warn("补发工单{}未找到商品{}的规格，跳过生成出库单", workOrder.getId(), workOrder.getGoodsId());
                    return;
                }
                sku = skus.get(0);
                skuId = sku.getId();
            } else {
                sku = goodsSkuService.getById(skuId);
            }
            int quantity = workOrder.getQuantity() == null || workOrder.getQuantity() < 1 ? 1 : workOrder.getQuantity();

            GoodsSkuInventoryVo item = new GoodsSkuInventoryVo();
            item.setGoodsId(workOrder.getGoodsId());
            item.setSkuId(skuId);
            item.setQuantity(quantity);

            StockOutCreateRequest request = new StockOutCreateRequest();
            request.setType(5); // 5=补发出库
            request.setSourceNo("WO" + workOrder.getId());
            request.setRemark("补发工单自动生成");
            request.setItemList(java.util.Collections.singletonList(item));
            stockOutService.createEntry(operatorId, userName, request);

            // 出库单只是出库轨迹，真实库存在这里扣减
            deductInventory(userName, workOrder, sku, skuId, quantity);
        } catch (Exception e) {
            // 出库单生成失败不阻断工单创建，由人工补出库
            log.error("补发工单{}生成出库单失败", workOrder.getId(), e);
        }
    }

    /**
     * 补发扣减库存：扣减 SKU 总库存 + 批次库存（先进先出），并记录库存流水
     */
    private void deductInventory(String userName, ErpWorkOrder workOrder, ErpGoodsSku sku, Long skuId, int quantity) {
        ErpGoodsInventory inventory = goodsInventoryService.getOne(new LambdaQueryWrapper<ErpGoodsInventory>()
                .eq(ErpGoodsInventory::getSkuId, skuId)
                .eq(ErpGoodsInventory::getIsDelete, 0)
                .last("limit 1"));
        if (inventory == null) {
            log.warn("补发工单{}：规格{}没有库存档案，本次未扣减库存", workOrder.getId(), skuId);
            return;
        }
        int current = inventory.getQuantity() == null ? 0 : inventory.getQuantity();
        if (current < quantity) {
            log.warn("补发工单{}：规格{}库存不足（当前{}，需要{}），本次未扣减库存", workOrder.getId(), skuId, current, quantity);
            return;
        }

        ErpGoodsInventory update = new ErpGoodsInventory();
        update.setId(inventory.getId());
        update.setQuantity(current - quantity);
        update.setUpdateBy(userName);
        update.setUpdateTime(new Date());
        goodsInventoryService.updateById(update);

        // 批次库存按先进先出扣减（批次是总库存的明细账）
        ErpGoodsInventoryBatch usedBatch = deductBatch(userName, skuId, quantity);

        ErpGoodsInventoryOperation operation = new ErpGoodsInventoryOperation();
        operation.setGoodsId(workOrder.getGoodsId());
        operation.setGoodsNum(inventory.getGoodsNum());
        operation.setSkuId(skuId);
        operation.setSkuCode(inventory.getSkuCode() == null ? "" : inventory.getSkuCode());
        operation.setType(2); // 2=减少库存
        operation.setInventoryId(Long.valueOf(inventory.getId()));
        operation.setQuantity(quantity);
        operation.setLockedQuantity(0);
        operation.setPrice(sku != null && sku.getPurPrice() != null ? sku.getPurPrice().doubleValue() : 0d);
        operation.setBizType(203); // 203=订单补发出库
        operation.setBizId(workOrder.getId());
        operation.setBizNum(workOrder.getOrderNum() == null ? "" : workOrder.getOrderNum());
        operation.setBizItemId(0L);
        operation.setStatus(1);
        operation.setRemark("补发工单#" + workOrder.getId() + " 自动扣减库存");
        operation.setCreateBy(userName);
        operation.setCreateTime(new Date());
        // 批次/仓库/仓位字段在表结构中为 NOT NULL，无批次时补默认值
        operation.setBatchId(0L);
        operation.setBatchNum("");
        operation.setWarehouseId(0L);
        operation.setPositionId(0L);
        operation.setPositionNum("");
        if (usedBatch != null) {
            operation.setBatchId(usedBatch.getId() == null ? 0L : usedBatch.getId());
            operation.setBatchNum(usedBatch.getBatchNum() == null ? "" : usedBatch.getBatchNum());
            operation.setWarehouseId(usedBatch.getWarehouseId() == null ? 0L : usedBatch.getWarehouseId());
            operation.setPositionId(usedBatch.getPositionId() == null ? 0L : usedBatch.getPositionId());
            operation.setPositionNum(usedBatch.getPositionNum() == null ? "" : usedBatch.getPositionNum());
        }
        goodsInventoryOperationMapper.insert(operation);
    }

    /**
     * 批次库存先进先出扣减，返回实际扣减到的批次
     */
    private ErpGoodsInventoryBatch deductBatch(String userName, Long skuId, int quantity) {
        List<ErpGoodsInventoryBatch> batches = goodsInventoryBatchService.querySkuBatchList(skuId);
        if (batches == null || batches.isEmpty()) {
            return null;
        }
        int remain = quantity;
        ErpGoodsInventoryBatch firstUsed = null;
        for (ErpGoodsInventoryBatch batch : batches) {
            if (remain <= 0) {
                break;
            }
            int batchQty = batch.getCurrentQty() == null ? 0 : batch.getCurrentQty();
            if (batchQty <= 0) {
                continue;
            }
            int take = Math.min(batchQty, remain);
            ErpGoodsInventoryBatch update = new ErpGoodsInventoryBatch();
            update.setId(batch.getId());
            update.setCurrentQty(batchQty - take);
            update.setUpdateBy(userName);
            update.setUpdateTime(new Date());
            goodsInventoryBatchService.updateById(update);
            if (firstUsed == null) {
                firstUsed = batch;
            }
            remain -= take;
        }
        return firstUsed;
    }
}
