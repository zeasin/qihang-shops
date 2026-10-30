package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpPromotionFee;
import cn.qihangerp.api.mapper.ErpPromotionFeeMapper;
import cn.qihangerp.api.service.ErpPromotionFeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 推广费服务实现
 */
@Service
public class ErpPromotionFeeServiceImpl extends ServiceImpl<ErpPromotionFeeMapper, ErpPromotionFee>
        implements ErpPromotionFeeService {

    @Override
    public PageResult<ErpPromotionFee> queryPageList(ErpPromotionFee bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpPromotionFee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(bo.getShopId() != null, ErpPromotionFee::getShopId, bo.getShopId());
        queryWrapper.eq(bo.getGoodsId() != null, ErpPromotionFee::getGoodsId, bo.getGoodsId());
        queryWrapper.eq(StringUtils.hasText(bo.getFeeMonth()), ErpPromotionFee::getFeeMonth, bo.getFeeMonth());
        queryWrapper.like(StringUtils.hasText(bo.getPlanName()), ErpPromotionFee::getPlanName, bo.getPlanName());
        queryWrapper.orderByDesc(ErpPromotionFee::getFeeMonth);
        queryWrapper.orderByDesc(ErpPromotionFee::getId);
        Page<ErpPromotionFee> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }
}
