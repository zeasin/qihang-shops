package cn.qihangerp.api.service.impl;

import cn.qihangerp.api.domain.ErpShopPlatform;
import cn.qihangerp.api.mapper.ErpShopPlatformMapper;
import cn.qihangerp.api.service.ErpShopPlatformService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 店铺平台 Service 实现
 *
 * @author qihang
 */
@AllArgsConstructor
@Service
public class ErpShopPlatformServiceImpl extends ServiceImpl<ErpShopPlatformMapper, ErpShopPlatform>
        implements ErpShopPlatformService {

    @Override
    public List<ErpShopPlatform> selectPlatformList() {
        return list(new LambdaQueryWrapper<ErpShopPlatform>().orderByAsc(ErpShopPlatform::getId));
    }

    @Override
    public ErpShopPlatform selectByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<ErpShopPlatform>()
                .eq(ErpShopPlatform::getCode, code)
                .last("limit 1"));
    }
}
