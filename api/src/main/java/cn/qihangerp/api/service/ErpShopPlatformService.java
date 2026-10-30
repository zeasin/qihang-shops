package cn.qihangerp.api.service;

import cn.qihangerp.api.domain.ErpShopPlatform;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 店铺平台 Service
 *
 * @author qihang
 */
public interface ErpShopPlatformService extends IService<ErpShopPlatform> {

    /**
     * 查询平台列表
     */
    List<ErpShopPlatform> selectPlatformList();

    /**
     * 根据平台编码查询
     */
    ErpShopPlatform selectByCode(String code);
}
