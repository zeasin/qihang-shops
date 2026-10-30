package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.ResultVo;
import cn.qihangerp.api.common.ResultVoEnum;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.domain.ErpGoods;
import cn.qihangerp.api.domain.ErpGoodsSku;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.domain.ErpOrderItem;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.request.ManualOrderBo;
import cn.qihangerp.api.service.ErpGoodsService;
import cn.qihangerp.api.service.ErpGoodsSkuService;
import cn.qihangerp.api.service.ErpOrderItemService;
import cn.qihangerp.api.service.ErpOrderService;
import cn.qihangerp.api.service.OrderImportService;
import cn.qihangerp.api.service.ShopService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单导入（Excel 表头映射）+ 手动建单服务实现
 *
 * 导入语义：重复订单号跳过；SKU 按规格编码→条码→商品编码 匹配，未匹配自动建档（成本留空，进完整度提示）；
 * 导入订单置为已完成/已发货（orderStatus=3, shipStatus=2），不干扰待发货流程。
 */
@Slf4j
@AllArgsConstructor
@Service
public class OrderImportServiceImpl implements OrderImportService {

    private final ErpOrderService orderService;
    private final ErpOrderItemService orderItemService;
    private final ErpGoodsService goodsService;
    private final ErpGoodsSkuService goodsSkuService;
    private final ShopService shopService;

    /** 系统字段清单（顺序即前端映射展示顺序） */
    public static final List<String[]> MAPPING_FIELDS = Arrays.asList(
            new String[]{"orderNum", "订单号", "1"},
            new String[]{"orderTime", "下单时间", "0"},
            new String[]{"payAmount", "实收金额", "0"},
            new String[]{"expressFee", "快递费", "0"},
            new String[]{"platformFee", "平台扣费(参考)", "0"},
            new String[]{"goodsTitle", "商品名称", "0"},
            new String[]{"skuNum", "规格编码/SKU", "0"},
            new String[]{"quantity", "数量", "0"},
            new String[]{"itemAmount", "商品金额", "0"},
            new String[]{"goodsPrice", "商品单价", "0"}
    );

    private static final DateTimeFormatter[] TIME_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    @Override
    public Map<String, Object> parse(MultipartFile file) throws IOException {
        List<List<String>> rows = ExcelHelper.readRows(file.getInputStream());
        Map<String, Object> result = new HashMap<>();
        if (rows.isEmpty()) {
            result.put("headers", new ArrayList<>());
            result.put("rowCount", 0);
            result.put("sampleRows", new ArrayList<>());
            return result;
        }
        result.put("headers", rows.get(0));
        result.put("rowCount", Math.max(rows.size() - 1, 0));
        result.put("sampleRows", rows.subList(1, Math.min(rows.size(), 6)));
        return result;
    }

    @Override
    public Map<String, Object> preview(Long shopId, String mappingJson, MultipartFile file) throws IOException {
        Map<String, Integer> mapping = parseMapping(mappingJson);
        List<List<String>> rows = ExcelHelper.readRows(file.getInputStream());
        if (rows.size() < 2) {
            throw new IOException("没有可导入的数据行");
        }

        Set<String> existingNums = loadExistingOrderNums(rows, mapping);
        Map<String, ErpGoodsSku> skuIndex = loadSkuIndex();

        List<Map<String, Object>> previewRows = new ArrayList<>();
        int unmatchedSku = 0;
        Set<String> unmatchedSkuKeys = new HashSet<>();
        int dupRows = 0;
        int limit = Math.min(rows.size(), 51); // 表头 + 前 50 行
        for (int i = 1; i < limit; i++) {
            ParsedRow pr = parseRow(rows.get(i), mapping, i);
            if (pr == null) {
                continue;
            }
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("row", pr.rowIndex + 1);
            vo.put("orderNum", pr.orderNum);
            vo.put("orderTime", pr.orderTime == null ? "" : pr.orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            vo.put("goodsTitle", pr.goodsTitle);
            vo.put("skuNum", pr.skuNum);
            vo.put("quantity", pr.quantity);
            vo.put("itemAmount", pr.itemAmount);
            vo.put("payAmount", pr.payAmount);
            vo.put("expressFee", pr.expressFee);
            vo.put("platformFee", pr.platformFee);
            boolean dup = existingNums.contains(pr.orderNum);
            vo.put("dup", dup);
            ErpGoodsSku sku = matchSku(skuIndex, pr.skuNum);
            vo.put("skuName", sku == null ? "" : sku.getGoodsName() + " " + nullToEmpty(sku.getSpecName()));
            vo.put("matched", sku != null);
            if (dup) {
                dupRows++;
            }
            if (sku == null && StringUtils.hasText(pr.skuNum)) {
                unmatchedSku++;
                unmatchedSkuKeys.add(pr.skuNum);
            }
            previewRows.add(vo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("headers", rows.get(0));
        result.put("previewRows", previewRows);
        result.put("totalRows", rows.size() - 1);
        result.put("dupRows", dupRows);
        result.put("unmatchedSku", unmatchedSku);
        result.put("autoCreateEstimate", unmatchedSkuKeys.size());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> commit(String userName, Long shopId, String mappingJson, MultipartFile file) throws IOException {
        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            throw new IOException("店铺不存在");
        }
        Map<String, Integer> mapping = parseMapping(mappingJson);
        List<List<String>> rows = ExcelHelper.readRows(file.getInputStream());
        if (rows.size() < 2) {
            throw new IOException("没有可导入的数据行");
        }

        Set<String> existingNums = loadExistingOrderNums(rows, mapping);
        Map<String, ErpGoodsSku> skuIndex = loadSkuIndex();

        // 按订单号分组（保持文件顺序）
        Map<String, List<ParsedRow>> groups = new LinkedHashMap<>();
        int badRows = 0;
        for (int i = 1; i < rows.size(); i++) {
            ParsedRow pr = parseRow(rows.get(i), mapping, i);
            if (pr == null) {
                badRows++;
                continue;
            }
            groups.computeIfAbsent(pr.orderNum, k -> new ArrayList<>()).add(pr);
        }

        int success = 0, skipped = 0, autoCreated = 0;
        int unmatchedCost = 0;
        Set<String> unmatchedSkuKeys = new HashSet<>();
        Date now = new Date();
        for (Map.Entry<String, List<ParsedRow>> entry : groups.entrySet()) {
            String orderNum = entry.getKey();
            List<ParsedRow> items = entry.getValue();
            if (existingNums.contains(orderNum)) {
                skipped++;
                continue;
            }
            ParsedRow first = items.get(0);
            ErpOrder order = new ErpOrder();
            order.setOrderNum(orderNum);
            order.setShopId(shopId);
            order.setShopType(shop.getType());
            order.setPayAmount(first.payAmount);
            order.setOrderAmount(first.payAmount);
            order.setExpressFee(first.expressFee);
            order.setPlatformFee(first.platformFee);
            double goodsAmount = 0;
            for (ParsedRow it : items) {
                goodsAmount += it.itemAmount == null ? 0 : it.itemAmount;
            }
            order.setGoodsAmount(goodsAmount);
            order.setOrderTime(first.orderTime);
            order.setOrderStatus(3);
            order.setShipStatus(2);
            order.setRefundStatus(1);
            order.setHasPushErp(0);
            order.setCreateTime(now);
            order.setCreateBy(userName);
            orderService.save(order);

            List<ErpOrderItem> orderItems = new ArrayList<>();
            for (ParsedRow pr : items) {
                ErpGoodsSku sku = matchSku(skuIndex, pr.skuNum);
                if (sku == null && StringUtils.hasText(pr.skuNum)) {
                    // 自动建档（成本留空，进完整度提示）
                    sku = autoCreateGoods(pr);
                    if (sku != null) {
                        skuIndex.put(skuKey(pr.skuNum), sku);
                        autoCreated++;
                    } else {
                        unmatchedSkuKeys.add(pr.skuNum);
                    }
                }
                ErpOrderItem item = new ErpOrderItem();
                item.setShopId(shopId);
                item.setOrderId(order.getId());
                item.setOrderNum(orderNum);
                item.setSubOrderNum(orderNum);
                item.setSkuId(pr.skuNum == null ? "" : pr.skuNum);
                item.setErpGoodsId(sku == null ? null : sku.getGoodsId());
                item.setErpSkuId(sku == null ? null : sku.getId());
                item.setGoodsTitle(StringUtils.hasText(pr.goodsTitle) ? pr.goodsTitle
                        : (sku == null ? null : sku.getGoodsName()));
                item.setGoodsNum(sku == null ? null : sku.getGoodsNum());
                item.setSkuNum(sku == null ? pr.skuNum : sku.getSpecNum());
                item.setQuantity(pr.quantity);
                item.setItemAmount(pr.itemAmount);
                item.setGoodsPrice(pr.goodsPrice == null ? 0.0 : pr.goodsPrice);
                item.setRefundStatus(1);
                item.setCreateTime(now);
                item.setCreateBy(userName);
                orderItems.add(item);
                if (sku == null || sku.getUnitCost() == null) {
                    unmatchedCost++;
                }
            }
            orderItemService.saveBatch(orderItems);
            success++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("skipped", skipped);
        result.put("badRows", badRows);
        result.put("autoCreated", autoCreated);
        result.put("unmatchedCost", unmatchedCost);
        result.put("unmatchedSkuKeys", unmatchedSkuKeys);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> createManual(String userName, ManualOrderBo bo) {
        if (bo.getShopId() == null) {
            return ResultVo.error(ResultVoEnum.ParamsError, "请选择店铺");
        }
        Shop shop = shopService.getById(bo.getShopId());
        if (shop == null) {
            return ResultVo.error(ResultVoEnum.ParamsError, "店铺不存在");
        }
        if (bo.getItemList() == null || bo.getItemList().isEmpty()) {
            return ResultVo.error(ResultVoEnum.ParamsError, "请至少添加一条商品明细");
        }
        if (bo.getPayAmount() == null) {
            return ResultVo.error(ResultVoEnum.ParamsError, "请填写实收金额");
        }
        if (!StringUtils.hasText(bo.getOrderNum())) {
            return ResultVo.error(ResultVoEnum.ParamsError, "请填写订单号");
        }
        if (!StringUtils.hasText(bo.getOrderTime())) {
            return ResultVo.error(ResultVoEnum.ParamsError, "请选择下单时间");
        }

        String orderNum = bo.getOrderNum().trim();
        Long exists = orderService.count(new LambdaQueryWrapper<ErpOrder>()
                
                .eq(ErpOrder::getOrderNum, orderNum));
        if (exists > 0) {
            return ResultVo.error(ResultVoEnum.DataExist, "订单号已存在：" + orderNum);
        }

        LocalDateTime orderTime = null;
        for (DateTimeFormatter fmt : TIME_FORMATS) {
            try {
                orderTime = LocalDateTime.parse(bo.getOrderTime().trim(), fmt);
                break;
            } catch (Exception ignored) {
            }
        }
        if (orderTime == null) {
            return ResultVo.error(ResultVoEnum.ParamsError, "下单时间格式不正确");
        }

        Date now = new Date();
        ErpOrder order = new ErpOrder();
        order.setOrderNum(orderNum);
        order.setShopId(bo.getShopId());
        order.setShopType(shop.getType());
        order.setPayAmount(bo.getPayAmount());
        order.setOrderAmount(bo.getPayAmount());
        order.setPlatformFee(bo.getPlatformFee());
        double goodsAmount = 0;
        for (ManualOrderBo.Item it : bo.getItemList()) {
            goodsAmount += it.getItemAmount() == null ? 0 : it.getItemAmount();
        }
        order.setGoodsAmount(goodsAmount);
        order.setOrderTime(orderTime);
        order.setOrderStatus(1);
        order.setShipStatus(0);
        order.setRefundStatus(1);
        order.setRemark(bo.getRemark());
        order.setCreateTime(now);
        order.setCreateBy(userName);
        orderService.save(order);

        List<ErpOrderItem> orderItems = new ArrayList<>();
        for (ManualOrderBo.Item it : bo.getItemList()) {
            if (it.getSkuId() == null) {
                return ResultVo.error(ResultVoEnum.ParamsError, "存在未选择商品的明细行");
            }
            ErpGoodsSku sku = goodsSkuService.getById(it.getSkuId());
            if (sku == null) {
                return ResultVo.error(ResultVoEnum.ParamsError, "商品规格不存在或无权使用");
            }
            ErpOrderItem item = new ErpOrderItem();
            item.setShopId(bo.getShopId());
            item.setOrderId(order.getId());
            item.setOrderNum(orderNum);
            item.setSubOrderNum(orderNum);
            item.setSkuId(String.valueOf(sku.getId()));
            item.setErpGoodsId(sku.getGoodsId());
            item.setErpSkuId(sku.getId());
            item.setGoodsTitle(sku.getGoodsName());
            item.setGoodsImg(sku.getColorImage());
            item.setGoodsNum(sku.getGoodsNum());
            item.setSkuNum(sku.getSpecNum());
            item.setGoodsSpec(sku.getSpecName());
            item.setQuantity(it.getQuantity() == null || it.getQuantity() < 1 ? 1 : it.getQuantity());
            item.setItemAmount(it.getItemAmount());
            item.setGoodsPrice(sku.getRetailPrice() == null ? 0.0 : sku.getRetailPrice().doubleValue());
            item.setRefundStatus(1);
            item.setCreateTime(now);
            item.setCreateBy(userName);
            orderItems.add(item);
        }
        orderItemService.saveBatch(orderItems);
        return ResultVo.success(order.getId() == null ? 0L : Long.valueOf(order.getId()));
    }

    // ==================== 内部方法 ====================

    private Map<String, Integer> parseMapping(String mappingJson) throws IOException {
        try {
            Map<String, Object> raw = JSON.parseObject(mappingJson, Map.class);
            if (raw == null || raw.get("orderNum") == null) {
                throw new IOException("映射缺少订单号列，请完成表头映射");
            }
            Map<String, Integer> mapping = new HashMap<>();
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                if (e.getValue() instanceof Number) {
                    mapping.put(e.getKey(), ((Number) e.getValue()).intValue());
                }
            }
            if (mapping.get("orderNum") == null) {
                throw new IOException("映射缺少订单号列，请完成表头映射");
            }
            return mapping;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("映射格式错误，请重新配置表头映射");
        }
    }

    private Set<String> loadExistingOrderNums(List<List<String>> rows, Map<String, Integer> mapping) {
        Integer col = mapping.get("orderNum");
        if (col == null) {
            return Collections.emptySet();
        }
        Set<String> nums = new LinkedHashSet<>();
        for (int i = 1; i < rows.size(); i++) {
            String num = ExcelHelper.get(rows.get(i), col);
            if (StringUtils.hasText(num)) {
                nums.add(num.trim());
            }
        }
        if (nums.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> numList = new ArrayList<>(nums);
        Set<String> existing = new HashSet<>();
        for (int i = 0; i < numList.size(); i += 500) {
            for (ErpOrder o : orderService.list(new LambdaQueryWrapper<ErpOrder>()
                    
                    .select(ErpOrder::getOrderNum)
                    .in(ErpOrder::getOrderNum, numList.subList(i, Math.min(i + 500, numList.size()))))) {
                existing.add(o.getOrderNum());
            }
        }
        return existing;
    }

    private Map<String, ErpGoodsSku> loadSkuIndex() {
        Map<String, ErpGoodsSku> index = new HashMap<>();
        for (ErpGoodsSku sku : goodsSkuService.list()) {
            if (StringUtils.hasText(sku.getSpecNum())) {
                index.putIfAbsent(skuKey(sku.getSpecNum()), sku);
            }
            if (StringUtils.hasText(sku.getBarCode())) {
                index.putIfAbsent(skuKey(sku.getBarCode()), sku);
            }
        }
        return index;
    }

    private String skuKey(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private ErpGoodsSku matchSku(Map<String, ErpGoodsSku> index, String skuNum) {
        if (!StringUtils.hasText(skuNum)) {
            return null;
        }
        return index.get(skuKey(skuNum));
    }

    private ParsedRow parseRow(List<String> row, Map<String, Integer> mapping, int rowIndex) {
        String orderNum = getCol(row, mapping.get("orderNum"));
        if (!StringUtils.hasText(orderNum)) {
            return null; // 无订单号的行跳过
        }
        ParsedRow pr = new ParsedRow();
        pr.rowIndex = rowIndex;
        pr.orderNum = orderNum.trim();
        String timeStr = getCol(row, mapping.get("orderTime"));
        if (StringUtils.hasText(timeStr)) {
            for (DateTimeFormatter fmt : TIME_FORMATS) {
                try {
                    pr.orderTime = LocalDateTime.parse(timeStr.trim(), fmt);
                    break;
                } catch (Exception ignored) {
                }
            }
        }
        pr.payAmount = getDouble(row, mapping.get("payAmount"));
        pr.expressFee = getDouble(row, mapping.get("expressFee"));
        pr.platformFee = getDouble(row, mapping.get("platformFee"));
        pr.goodsTitle = getCol(row, mapping.get("goodsTitle"));
        pr.skuNum = getCol(row, mapping.get("skuNum"));
        pr.quantity = getInt(row, mapping.get("quantity"));
        pr.itemAmount = getDouble(row, mapping.get("itemAmount"));
        pr.goodsPrice = getDouble(row, mapping.get("goodsPrice"));
        return pr;
    }

    /**
     * 自动建档：erp_goods + erp_goods_sku（成本留空）
     */
    private ErpGoodsSku autoCreateGoods(ParsedRow pr) {
        try {
            String name = StringUtils.hasText(pr.goodsTitle) ? pr.goodsTitle : pr.skuNum;
            // 同名商品已存在则挂到该商品下
            ErpGoods goods = goodsService.getOne(new LambdaQueryWrapper<ErpGoods>()
                    
                    .eq(ErpGoods::getName, name)
                    .last("limit 1"));
            if (goods == null) {
                goods = new ErpGoods();
                goods.setName(name);
                goods.setNumber(pr.skuNum);
                goods.setStatus(0);
                goods.setDisable(0);
                goods.setCreateTime(new Date());
                goodsService.save(goods);
            }
            ErpGoodsSku sku = new ErpGoodsSku();
            sku.setGoodsId(goods.getId());
            sku.setGoodsName(name);
            sku.setGoodsNum(goods.getNumber());
            sku.setSpecNum(pr.skuNum);
            sku.setStatus(1);
            sku.setDisable(0);
            goodsSkuService.save(sku);
            return sku;
        } catch (Exception e) {
            log.error("订单导入自动建档失败 skuNum={}", pr.skuNum, e);
            return null;
        }
    }

    private String getCol(List<String> row, Integer col) {
        return col == null ? null : ExcelHelper.get(row, col);
    }

    private Double getDouble(List<String> row, Integer col) {
        String v = getCol(row, col);
        if (!StringUtils.hasText(v)) {
            return null;
        }
        try {
            double d = Double.parseDouble(v.replace(",", "").replace("¥", "").replace("￥", "").trim());
            return Double.isNaN(d) ? null : d;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getInt(List<String> row, Integer col) {
        Double d = getDouble(row, col);
        return d == null ? null : (int) Math.round(d);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /** 解析后的行 */
    private static class ParsedRow {
        int rowIndex;
        String orderNum;
        LocalDateTime orderTime;
        Double payAmount;
        Double expressFee;
        Double platformFee;
        String goodsTitle;
        String skuNum;
        Integer quantity;
        Double itemAmount;
        Double goodsPrice;
    }
}
