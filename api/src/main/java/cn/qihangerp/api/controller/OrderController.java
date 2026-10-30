package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.TableDataInfo;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.request.OrderSearchRequest;
import cn.qihangerp.api.request.OrderShipRequest;
import cn.qihangerp.api.service.ErpOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 订单库Controller
 *
 * @author qihang
 * @date 2023-12-31
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController
{
    @Autowired
    private ErpOrderService orderService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('shop:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderSearchRequest order, PageQuery pageQuery)
    {
        var pageList = orderService.queryPageList(order,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpOrder erpOrder = orderService.queryDetailById(id);

        return success(erpOrder);
    }

    /**
     * 订单发货：填写发货成本，记录发货时间并按当前商品成本档案写入明细成本快照
     */
    @PostMapping("/ship")
    public AjaxResult ship(@RequestBody OrderShipRequest request) {
        try {
            int count = orderService.shipOrders(getUsername(), request.getIds(), request.getExpressFee(), request.getWaybillCode());
            return AjaxResult.success("发货成功", count);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
