package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.domain.ErpPromotionFee;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.service.ErpPromotionFeeService;
import cn.qihangerp.api.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 费用中心-推广费（按链接/店铺、月份录入，利润按实收占比分摊）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/promotionFee")
public class PromotionFeeController extends BaseController {
    private final ErpPromotionFeeService promotionFeeService;
    private final ShopService shopService;

    @GetMapping("/list")
    public TableDataInfo list(ErpPromotionFee bo, PageQuery pageQuery) {
        PageResult<ErpPromotionFee> pageResult = promotionFeeService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @Log(title = "推广费用", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpPromotionFee fee) {
        if (fee.getShopId() == null || !org.springframework.util.StringUtils.hasText(fee.getFeeMonth())
                || fee.getAmount() == null || fee.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("请填写店铺、月份和金额");
        }
        fee.setCreateBy(getUsername());
        fee.setCreateTime(new Date());
        return toAjax(promotionFeeService.save(fee));
    }

    @Log(title = "推广费用", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpPromotionFee fee) {
        if (fee.getId() == null) {
            return AjaxResult.error("缺少ID");
        }
        ErpPromotionFee exists = promotionFeeService.getById(fee.getId());
        if (exists == null) {
            return AjaxResult.error("记录不存在");
        }
        fee.setUpdateBy(getUsername());
        fee.setUpdateTime(new Date());
        return toAjax(promotionFeeService.updateById(fee));
    }

    @Log(title = "推广费用", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(promotionFeeService.remove(new LambdaQueryWrapper<ErpPromotionFee>()
                .in(ErpPromotionFee::getId, Arrays.stream(ids).toList())
                ));
    }

    /**
     * Excel 导入（模板：店铺、链接名称、计划名、月份、金额）
     */
    @Log(title = "推广费用", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) {
        Map<String, Long> shopMap = new HashMap<>();
        for (Shop shop : shopService.list(new LambdaQueryWrapper<Shop>())) {
            try {
                shopMap.put(shop.getName(), Long.valueOf(shop.getId()));
            } catch (NumberFormatException ignored) {
            }
        }
        List<List<String>> rows;
        try {
            rows = ExcelHelper.readRows(file.getInputStream());
        } catch (Exception e) {
            log.error("推广费导入解析失败", e);
            return AjaxResult.error("文件解析失败，请使用模板 Excel");
        }
        if (rows.size() < 2) {
            return AjaxResult.error("没有可导入的数据");
        }
        int success = 0, fail = 0;
        StringBuilder errMsg = new StringBuilder();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            try {
                String shopName = ExcelHelper.get(row, 0);
                String goodsName = ExcelHelper.get(row, 1);
                String planName = ExcelHelper.get(row, 2);
                String feeMonth = ExcelHelper.get(row, 3);
                String amountStr = ExcelHelper.get(row, 4);
                BigDecimal amount = amountStr == null ? null : new BigDecimal(amountStr);
                Long shopId = shopName == null ? null : shopMap.get(shopName.trim());
                if (shopId == null || !org.springframework.util.StringUtils.hasText(feeMonth)
                        || amount == null || amount.signum() < 0) {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：店铺/月份/金额非法；");
                    continue;
                }
                ErpPromotionFee fee = new ErpPromotionFee();
                fee.setShopId(shopId);
                fee.setGoodsName(goodsName);
                fee.setPlanName(planName);
                fee.setFeeMonth(feeMonth.trim());
                fee.setAmount(amount);
                fee.setCreateBy(getUsername());
                fee.setCreateTime(new Date());
                promotionFeeService.save(fee);
                success++;
            } catch (Exception e) {
                fail++;
                errMsg.append("第").append(i + 1).append("行：解析异常；");
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("message", errMsg.length() > 500 ? errMsg.substring(0, 500) : errMsg.toString());
        return AjaxResult.success(result);
    }
}
