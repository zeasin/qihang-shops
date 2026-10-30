package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.domain.ErpGoods;
import cn.qihangerp.api.domain.ErpGoodsSku;
import cn.qihangerp.api.service.ErpGoodsService;
import cn.qihangerp.api.service.ErpGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品成本（小店管家：SKU 成本价档案）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/goodsCost")
public class GoodsCostController extends BaseController {
    private final ErpGoodsService goodsService;
    private final ErpGoodsSkuService goodsSkuService;

    /**
     * SKU 成本档案列表（关键字/未填成本筛选）
     */
    @GetMapping("/skuList")
    public TableDataInfo skuList(ErpGoodsSku bo, PageQuery pageQuery) {
        PageResult<ErpGoodsSku> pageResult = goodsService.querySkuPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    /**
     * 行内更新成本价/售价
     */
    @PutMapping("/skuCost")
    public AjaxResult updateCost(@RequestBody ErpGoodsSku body) {
        if (body.getId() == null) {
            return AjaxResult.error("缺少规格ID");
        }
        ErpGoodsSku exists = goodsSkuService.getById(body.getId());
        if (exists == null) {
            return AjaxResult.error("规格不存在");
        }
        if (body.getUnitCost() != null && body.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("成本价不能为负数");
        }
        ErpGoodsSku update = new ErpGoodsSku();
        update.setId(body.getId());
        update.setUnitCost(body.getUnitCost());
        if (body.getRetailPrice() != null) {
            update.setRetailPrice(body.getRetailPrice());
        }
        return toAjax(goodsSkuService.updateById(update));
    }

    /**
     * Excel 导入成本（模板：商品名称、规格名称、规格编码、商品编码、成本价、售价）
     * 按规格编码→商品编码+规格名 匹配更新；不存在则自动建档（含成本价）
     */
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) {
        List<List<String>> rows;
        try {
            rows = ExcelHelper.readRows(file.getInputStream());
        } catch (Exception e) {
            log.error("商品成本导入解析失败", e);
            return AjaxResult.error("文件解析失败，请使用模板 Excel");
        }
        if (rows.size() < 2) {
            return AjaxResult.error("没有可导入的数据");
        }
        // 预加载本租户 SKU 索引
        Map<String, ErpGoodsSku> index = new HashMap<>();
        for (ErpGoodsSku sku : goodsSkuService.list(new LambdaQueryWrapper<ErpGoodsSku>()
                )) {
            if (StringUtils.hasText(sku.getSpecNum())) {
                index.putIfAbsent(key(sku.getSpecNum()), sku);
            }
            if (StringUtils.hasText(sku.getBarCode())) {
                index.putIfAbsent(key(sku.getBarCode()), sku);
            }
        }
        int updated = 0, created = 0, fail = 0;
        StringBuilder errMsg = new StringBuilder();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            try {
                String goodsName = ExcelHelper.get(row, 0);
                String specName = ExcelHelper.get(row, 1);
                String specNum = ExcelHelper.get(row, 2);
                String goodsNum = ExcelHelper.get(row, 3);
                String costStr = ExcelHelper.get(row, 4);
                String retailStr = ExcelHelper.get(row, 5);
                BigDecimal cost = parseDecimal(costStr);
                BigDecimal retail = parseDecimal(retailStr);
                if (!StringUtils.hasText(specNum) || cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：规格编码或成本价非法；");
                    continue;
                }
                ErpGoodsSku sku = index.get(key(specNum));
                if (sku == null && StringUtils.hasText(goodsNum)) {
                    sku = goodsSkuService.getOne(new LambdaQueryWrapper<ErpGoodsSku>()
                            
                            .eq(ErpGoodsSku::getGoodsNum, goodsNum.trim())
                            .eq(StringUtils.hasText(specName), ErpGoodsSku::getSpecName, specName)
                            .last("limit 1"));
                }
                if (sku != null) {
                    ErpGoodsSku update = new ErpGoodsSku();
                    update.setId(sku.getId());
                    update.setUnitCost(cost);
                    if (retail != null) {
                        update.setRetailPrice(retail);
                    }
                    goodsSkuService.updateById(update);
                    updated++;
                } else {
                    // 自动建档（含成本价）
                    String name = StringUtils.hasText(goodsName) ? goodsName : specNum;
                    ErpGoods goods = goodsService.getOne(new LambdaQueryWrapper<ErpGoods>()
                            
                            .eq(ErpGoods::getName, name)
                            .last("limit 1"));
                    if (goods == null) {
                        goods = new ErpGoods();
                        goods.setName(name);
                        goods.setNumber(StringUtils.hasText(goodsNum) ? goodsNum.trim() : specNum.trim());
                        goods.setStatus(0);
                        goods.setDisable(0);
                        goods.setCreateTime(new Date());
                        goodsService.save(goods);
                    }
                    ErpGoodsSku skuNew = new ErpGoodsSku();
                    skuNew.setGoodsId(goods.getId());
                    skuNew.setGoodsName(name);
                    skuNew.setGoodsNum(goods.getNumber());
                    skuNew.setSpecName(specName);
                    skuNew.setSpecNum(specNum.trim());
                    skuNew.setUnitCost(cost);
                    if (retail != null) {
                        skuNew.setRetailPrice(retail);
                    }
                    skuNew.setStatus(1);
                    skuNew.setDisable(0);
                    goodsSkuService.save(skuNew);
                    index.put(key(skuNew.getSpecNum()), skuNew);
                    created++;
                }
            } catch (Exception e) {
                fail++;
                errMsg.append("第").append(i + 1).append("行：解析异常；");
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("updated", updated);
        result.put("created", created);
        result.put("fail", fail);
        result.put("message", errMsg.length() > 500 ? errMsg.substring(0, 500) : errMsg.toString());
        return AjaxResult.success(result);
    }

    private BigDecimal parseDecimal(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        try {
            return new BigDecimal(s.replace(",", "").replace("¥", "").replace("￥", "").trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String key(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
}
