package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.common.utils.IdUtils;
import cn.qihangerp.api.domain.ErpPlatformBill;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.service.ErpPlatformBillService;
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
 * 费用中心-对账单（平台扣费/退款的数据来源）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/platformBill")
public class PlatformBillController extends BaseController {
    private final ErpPlatformBillService platformBillService;
    private final ShopService shopService;

    @GetMapping("/list")
    public TableDataInfo list(ErpPlatformBill bo, PageQuery pageQuery) {
        PageResult<ErpPlatformBill> pageResult = platformBillService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    /**
     * 手动补录单条账单明细
     */
    @Log(title = "平台账单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpPlatformBill bill) {
        if (bill.getShopId() == null || !StringUtils.hasText(bill.getBillMonth())
                || bill.getIncomeType() == null
                || bill.getAmount() == null || bill.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("请填写店铺、账期、收支和金额");
        }
        bill.setMatchStatus(ErpPlatformBill.MATCH_UNMATCHED);
        bill.setCreateBy(getUsername());
        bill.setCreateTime(new Date());
        boolean saved = platformBillService.save(bill);
        if (saved && StringUtils.hasText(bill.getOrderNum())) {
            platformBillService.matchOrders();
        }
        return toAjax(saved);
    }

    /**
     * 自动匹配：按订单号关联订单
     */
    @Log(title = "平台账单", businessType = BusinessType.UPDATE)
    @PutMapping("/match")
    public AjaxResult match() {
        int matched = platformBillService.matchOrders();
        return AjaxResult.success("本次匹配 " + matched + " 笔", matched);
    }

    /**
     * 删除明细
     */
    @Log(title = "平台账单", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(platformBillService.remove(new LambdaQueryWrapper<ErpPlatformBill>()
                .in(ErpPlatformBill::getId, Arrays.stream(ids).toList())
                ));
    }

    /**
     * 按上传批次整批删除（传错重传）
     */
    @Log(title = "平台账单", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{batchNo}")
    public AjaxResult removeBatch(@PathVariable String batchNo) {
        return toAjax(platformBillService.remove(new LambdaQueryWrapper<ErpPlatformBill>()
                .eq(ErpPlatformBill::getBatchNo, batchNo)
                ));
    }

    /**
     * 上传对账单 Excel
     * 表头：订单号、业务类型、收支（收入/支出）、金额、发生时间
     */
    @Log(title = "平台账单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file,
                                  @RequestParam Long shopId,
                                  @RequestParam String billMonth) {
        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            return AjaxResult.error("店铺不存在");
        }
        if (!StringUtils.hasText(billMonth) || !billMonth.matches("\\d{4}-\\d{2}")) {
            return AjaxResult.error("账期格式应为 yyyy-MM");
        }
        List<List<String>> rows;
        try {
            rows = ExcelHelper.readRows(file.getInputStream());
        } catch (Exception e) {
            log.error("对账单导入解析失败", e);
            return AjaxResult.error("文件解析失败，请检查表头：订单号、业务类型、收支、金额、发生时间");
        }
        if (rows.size() < 2) {
            return AjaxResult.error("没有可导入的数据");
        }
        String batchNo = IdUtils.simpleUUID().substring(0, 16).toUpperCase();
        int success = 0, fail = 0;
        StringBuilder errMsg = new StringBuilder();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            try {
                String orderNum = ExcelHelper.get(row, 0);
                String bizType = ExcelHelper.get(row, 1);
                String incomeStr = ExcelHelper.get(row, 2);
                String amountStr = ExcelHelper.get(row, 3);
                String bizTimeStr = ExcelHelper.get(row, 4);
                BigDecimal amount = amountStr == null ? null : new BigDecimal(amountStr).abs();
                Integer incomeType;
                if ("收入".equalsIgnoreCase(incomeStr)) {
                    incomeType = ErpPlatformBill.INCOME_TYPE_IN;
                } else if ("支出".equalsIgnoreCase(incomeStr)) {
                    incomeType = ErpPlatformBill.INCOME_TYPE_OUT;
                } else {
                    incomeType = null;
                }
                if (amount == null || amount.signum() == 0 || incomeType == null) {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：金额或收支非法；");
                    continue;
                }
                ErpPlatformBill bill = new ErpPlatformBill();
                bill.setShopId(shopId);
                bill.setShopType(shop.getType());
                bill.setBillMonth(billMonth);
                bill.setOrderNum(orderNum);
                bill.setBizType(bizType);
                bill.setIncomeType(incomeType);
                bill.setAmount(amount);
                bill.setBizTime(StringUtils.hasText(bizTimeStr) ? parseDate(bizTimeStr) : null);
                bill.setMatchStatus(ErpPlatformBill.MATCH_UNMATCHED);
                bill.setBatchNo(batchNo);
                bill.setCreateBy(getUsername());
                bill.setCreateTime(new Date());
                platformBillService.save(bill);
                success++;
            } catch (Exception e) {
                fail++;
                errMsg.append("第").append(i + 1).append("行：解析异常；");
            }
        }
        // 入库后尝试自动匹配
        int matched = platformBillService.matchOrders();
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("matched", matched);
        result.put("batchNo", batchNo);
        result.put("message", errMsg.length() > 500 ? errMsg.substring(0, 500) : errMsg.toString());
        return AjaxResult.success(result);
    }

    private Date parseDate(String s) {
        for (String pattern : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd"}) {
            try {
                return new java.text.SimpleDateFormat(pattern).parse(s.trim());
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
