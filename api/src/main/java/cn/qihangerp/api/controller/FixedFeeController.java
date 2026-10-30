package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.domain.ErpShopFixedFee;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.service.ErpShopFixedFeeService;
import cn.qihangerp.api.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 费用中心-固定费用（直接计入店铺净利润，不参与链接分摊）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/fixedFee")
public class FixedFeeController extends BaseController {
    private final ErpShopFixedFeeService fixedFeeService;
    private final ShopService shopService;

    @GetMapping("/list")
    public TableDataInfo list(ErpShopFixedFee bo, PageQuery pageQuery) {
        PageResult<ErpShopFixedFee> pageResult = fixedFeeService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @Log(title = "固定费用", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpShopFixedFee fee) {
        if (fee.getShopId() == null || !StringUtils.hasText(fee.getFeeMonth())
                || !StringUtils.hasText(fee.getFeeType())
                || fee.getAmount() == null || fee.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("请填写店铺、月份、费用项和金额");
        }
        fee.setCreateBy(getUsername());
        fee.setCreateTime(new Date());
        return toAjax(fixedFeeService.save(fee));
    }

    @Log(title = "固定费用", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpShopFixedFee fee) {
        if (fee.getId() == null) {
            return AjaxResult.error("缺少ID");
        }
        ErpShopFixedFee exists = fixedFeeService.getById(fee.getId());
        if (exists == null) {
            return AjaxResult.error("记录不存在");
        }
        fee.setUpdateBy(getUsername());
        fee.setUpdateTime(new Date());
        return toAjax(fixedFeeService.updateById(fee));
    }

    @Log(title = "固定费用", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(fixedFeeService.remove(new LambdaQueryWrapper<ErpShopFixedFee>()
                .in(ErpShopFixedFee::getId, Arrays.stream(ids).toList())
                ));
    }

    /**
     * Excel 导入（模板：店铺、费用项、月份、金额、备注）
     */
    @Log(title = "固定费用", businessType = BusinessType.IMPORT)
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
            log.error("固定费用导入解析失败", e);
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
                String feeType = ExcelHelper.get(row, 1);
                String feeMonth = ExcelHelper.get(row, 2);
                String amountStr = ExcelHelper.get(row, 3);
                String remark = ExcelHelper.get(row, 4);
                BigDecimal amount = amountStr == null ? null : new BigDecimal(amountStr);
                Long shopId = shopName == null ? null : shopMap.get(shopName.trim());
                if (shopId == null || !StringUtils.hasText(feeType) || !StringUtils.hasText(feeMonth)
                        || amount == null || amount.signum() < 0) {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：店铺/费用项/月份/金额非法；");
                    continue;
                }
                ErpShopFixedFee fee = new ErpShopFixedFee();
                fee.setShopId(shopId);
                fee.setFeeType(feeType.trim());
                fee.setFeeMonth(feeMonth.trim());
                fee.setAmount(amount);
                fee.setRemark(remark);
                fee.setCreateBy(getUsername());
                fee.setCreateTime(new Date());
                fixedFeeService.save(fee);
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
