package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpShopFixedFee;
import cn.qihangerp.api.mapper.ErpShopFixedFeeMapper;
import cn.qihangerp.api.service.ErpShopFixedFeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 店铺固定费用服务实现
 */
@Service
public class ErpShopFixedFeeServiceImpl extends ServiceImpl<ErpShopFixedFeeMapper, ErpShopFixedFee>
        implements ErpShopFixedFeeService {

    @Override
    public PageResult<ErpShopFixedFee> queryPageList(ErpShopFixedFee bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpShopFixedFee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(bo.getShopId() != null, ErpShopFixedFee::getShopId, bo.getShopId());
        queryWrapper.eq(StringUtils.hasText(bo.getFeeMonth()), ErpShopFixedFee::getFeeMonth, bo.getFeeMonth());
        queryWrapper.eq(StringUtils.hasText(bo.getFeeType()), ErpShopFixedFee::getFeeType, bo.getFeeType());
        queryWrapper.orderByDesc(ErpShopFixedFee::getFeeMonth);
        queryWrapper.orderByDesc(ErpShopFixedFee::getId);
        Page<ErpShopFixedFee> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }
}
