package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpPromotionFee;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 推广费服务
 */
public interface ErpPromotionFeeService extends IService<ErpPromotionFee> {

    /**
     * 分页查询（月份/店铺/计划）
     */
    PageResult<ErpPromotionFee> queryPageList(ErpPromotionFee bo, PageQuery pageQuery);
}
