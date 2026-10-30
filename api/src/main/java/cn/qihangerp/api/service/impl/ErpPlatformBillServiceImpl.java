package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpOrder;
import cn.qihangerp.api.domain.ErpPlatformBill;
import cn.qihangerp.api.mapper.ErpPlatformBillMapper;
import cn.qihangerp.api.service.ErpOrderService;
import cn.qihangerp.api.service.ErpPlatformBillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平台对账单明细服务实现
 */
@AllArgsConstructor
@Service
public class ErpPlatformBillServiceImpl extends ServiceImpl<ErpPlatformBillMapper, ErpPlatformBill>
        implements ErpPlatformBillService {

    private final ErpOrderService orderService;

    @Override
    public PageResult<ErpPlatformBill> queryPageList(ErpPlatformBill bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpPlatformBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(bo.getShopId() != null, ErpPlatformBill::getShopId, bo.getShopId());
        queryWrapper.eq(StringUtils.hasText(bo.getBillMonth()), ErpPlatformBill::getBillMonth, bo.getBillMonth());
        queryWrapper.eq(bo.getMatchStatus() != null, ErpPlatformBill::getMatchStatus, bo.getMatchStatus());
        queryWrapper.eq(StringUtils.hasText(bo.getBizType()), ErpPlatformBill::getBizType, bo.getBizType());
        queryWrapper.eq(StringUtils.hasText(bo.getBatchNo()), ErpPlatformBill::getBatchNo, bo.getBatchNo());
        queryWrapper.like(StringUtils.hasText(bo.getOrderNum()), ErpPlatformBill::getOrderNum, bo.getOrderNum());
        queryWrapper.orderByDesc(ErpPlatformBill::getId);
        Page<ErpPlatformBill> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public int matchOrders() {
        // 取未匹配且有订单号的账单明细
        List<ErpPlatformBill> unmatched = this.list(new LambdaQueryWrapper<ErpPlatformBill>()
                
                .eq(ErpPlatformBill::getMatchStatus, ErpPlatformBill.MATCH_UNMATCHED)
                .isNotNull(ErpPlatformBill::getOrderNum)
                .ne(ErpPlatformBill::getOrderNum, ""));
        if (unmatched.isEmpty()) {
            return 0;
        }
        List<String> orderNums = unmatched.stream()
                .map(ErpPlatformBill::getOrderNum)
                .distinct()
                .collect(Collectors.toList());
        // 按订单号查系统订单
        Map<String, ErpOrder> orderMap = orderService.list(new LambdaQueryWrapper<ErpOrder>()
                        
                        .in(ErpOrder::getOrderNum, orderNums))
                .stream()
                .collect(Collectors.toMap(ErpOrder::getOrderNum, Function.identity(), (a, b) -> a));
        int matched = 0;
        for (ErpPlatformBill bill : unmatched) {
            ErpOrder order = orderMap.get(bill.getOrderNum());
            if (order != null) {
                ErpPlatformBill update = new ErpPlatformBill();
                update.setId(bill.getId());
                update.setMatchStatus(ErpPlatformBill.MATCH_MATCHED);
                update.setOrderId(order.getId());
                this.updateById(update);
                matched++;
            }
        }
        return matched;
    }
}
