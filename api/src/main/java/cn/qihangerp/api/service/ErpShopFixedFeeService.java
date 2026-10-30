package cn.qihangerp.api.service;

import cn.qihangerp.api.common.PageQuery;
import cn.qihangerp.api.common.PageResult;
import cn.qihangerp.api.domain.ErpShopFixedFee;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 店铺固定费用服务
 */
public interface ErpShopFixedFeeService extends IService<ErpShopFixedFee> {

    /**
     * 分页查询（月份/店铺/费用项）
     */
    PageResult<ErpShopFixedFee> queryPageList(ErpShopFixedFee bo, PageQuery pageQuery);
}
