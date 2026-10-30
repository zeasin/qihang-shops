package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelHelper;
import cn.qihangerp.api.domain.ErpWorkOrder;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.service.ErpWorkOrderService;
import cn.qihangerp.api.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 工单（售后支出记账）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/workOrder")
public class WorkOrderController extends BaseController {
    private final ErpWorkOrderService workOrderService;
    private final ShopService shopService;

    /**
     * 工单列表（待处理置顶）
     */
    @GetMapping("/list")
    public TableDataInfo list(ErpWorkOrder bo, PageQuery pageQuery) {
        PageResult<ErpWorkOrder> pageResult = workOrderService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    /**
     * 速记工单：type + amount 必填，其余可选
     */
    @Log(title = "工单管理", businessType = BusinessType.INSERT)
    @PostMapping("/quick")
    public AjaxResult quick(@RequestBody ErpWorkOrder workOrder) {
        if (workOrder.getAmount() == null || workOrder.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("请填写金额");
        }
        ResultVo<Long> result = workOrderService.createWorkOrder(getUserId(), getUsername(), workOrder);
        return result.getCode() == ResultVoEnum.SUCCESS.getIndex()
                ? AjaxResult.success("已记录", result.getData())
                : AjaxResult.error(result.getMsg());
    }

    /**
     * 完整新建工单（含凭证/备注/店铺）
     */
    @Log(title = "工单管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpWorkOrder workOrder) {
        ResultVo<Long> result = workOrderService.createWorkOrder(getUserId(), getUsername(), workOrder);
        return result.getCode() == ResultVoEnum.SUCCESS.getIndex()
                ? AjaxResult.success("新增成功", result.getData())
                : AjaxResult.error(result.getMsg());
    }

    /**
     * 编辑工单（仅待处理可编辑）
     */
    @Log(title = "工单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody ErpWorkOrder workOrder) {
        if (workOrder.getId() == null) {
            return AjaxResult.error("缺少工单ID");
        }
        ErpWorkOrder exists = workOrderService.getById(workOrder.getId());
        if (exists == null) {
            return AjaxResult.error("工单不存在");
        }
        if (exists.getStatus() != ErpWorkOrder.STATUS_PENDING) {
            return AjaxResult.error("已结算工单不可编辑，请先撤销结算");
        }
        ErpWorkOrder update = new ErpWorkOrder();
        update.setId(workOrder.getId());
        update.setShopId(workOrder.getShopId());
        update.setOrderNum(workOrder.getOrderNum());
        update.setType(workOrder.getType());
        update.setAmount(workOrder.getAmount());
        update.setVoucher(workOrder.getVoucher());
        update.setRemark(workOrder.getRemark());
        update.setUpdateBy(getUsername());
        update.setUpdateTime(new Date());
        return toAjax(workOrderService.updateById(update));
    }

    /**
     * 删除工单（仅待处理可删）
     */
    @Log(title = "工单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<ErpWorkOrder> list = workOrderService.listByIds(Arrays.stream(ids).toList());
        for (ErpWorkOrder wo : list) {
            if (wo.getStatus() != ErpWorkOrder.STATUS_PENDING) {
                return AjaxResult.error("已结算工单不可删除，请先撤销结算");
            }
        }
        return toAjax(workOrderService.removeByIds(Arrays.stream(ids).toList()));
    }

    /**
     * 批量结算（结算后金额锁定进利润计算）
     */
    @Log(title = "工单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/settle/{ids}")
    public AjaxResult settle(@PathVariable Long[] ids) {
        return toAjax(workOrderService.update(new LambdaUpdateWrapper<ErpWorkOrder>()
                .in(ErpWorkOrder::getId, Arrays.stream(ids).toList())
                .eq(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_PENDING)
                .set(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_SETTLED)
                .set(ErpWorkOrder::getSettleTime, new Date())
                .set(ErpWorkOrder::getUpdateBy, getUsername())
                .set(ErpWorkOrder::getUpdateTime, new Date())));
    }

    /**
     * 撤销结算（解锁金额，回待处理）
     */
    @Log(title = "工单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/revoke/{id}")
    public AjaxResult revoke(@PathVariable Long id) {
        return toAjax(workOrderService.update(new LambdaUpdateWrapper<ErpWorkOrder>()
                .eq(ErpWorkOrder::getId, id)
                .eq(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_SETTLED)
                .set(ErpWorkOrder::getStatus, ErpWorkOrder.STATUS_PENDING)
                .set(ErpWorkOrder::getSettleTime, null)
                .set(ErpWorkOrder::getUpdateBy, getUsername())
                .set(ErpWorkOrder::getUpdateTime, new Date())));
    }

    /**
     * Excel 批量导入（售后明细）
     * 表头：订单号、类型、金额、店铺、备注（类型为中文：补款/返现/补发/仅退款/退货退款/其他）
     */
    @Log(title = "工单管理", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) {
        // 店铺名 → id
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
            log.error("工单导入解析失败", e);
            return AjaxResult.error("文件解析失败，请使用模板 Excel");
        }
        if (rows.size() < 2) {
            return AjaxResult.error("没有可导入的数据");
        }
        int success = 0, fail = 0;
        StringBuilder errMsg = new StringBuilder();
        String userName = getUsername();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            try {
                String orderNum = ExcelHelper.get(row, 0);
                String typeStr = ExcelHelper.get(row, 1);
                String amountStr = ExcelHelper.get(row, 2);
                String shopName = ExcelHelper.get(row, 3);
                String remark = ExcelHelper.get(row, 4);
                Integer type = parseType(typeStr);
                BigDecimal amount = amountStr == null || amountStr.isEmpty() ? null : new BigDecimal(amountStr);
                if (type == null || amount == null || amount.signum() < 0) {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：类型或金额非法；");
                    continue;
                }
                ErpWorkOrder wo = new ErpWorkOrder();
                wo.setOrderNum(orderNum);
                wo.setType(type);
                wo.setAmount(amount);
                wo.setShopId(shopName == null ? null : shopMap.get(shopName));
                wo.setRemark(remark);
                ResultVo<Long> result = workOrderService.createWorkOrder(getUserId(), userName, wo);
                if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                    success++;
                } else {
                    fail++;
                    errMsg.append("第").append(i + 1).append("行：").append(result.getMsg()).append("；");
                }
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

    private Integer parseType(String typeStr) {
        if (typeStr == null) {
            return null;
        }
        return switch (typeStr.trim()) {
            case "补款" -> ErpWorkOrder.TYPE_REISSUE_MONEY;
            case "返现" -> ErpWorkOrder.TYPE_CASHBACK;
            case "补发" -> ErpWorkOrder.TYPE_RESEND;
            case "仅退款" -> ErpWorkOrder.TYPE_REFUND_ONLY;
            case "退货退款" -> ErpWorkOrder.TYPE_RETURN_REFUND;
            case "其他" -> ErpWorkOrder.TYPE_OTHER;
            default -> null;
        };
    }
}
