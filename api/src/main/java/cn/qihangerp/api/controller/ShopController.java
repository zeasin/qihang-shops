package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.domain.ErpShopPlatform;
import cn.qihangerp.api.domain.Shop;
import cn.qihangerp.api.service.ErpShopPlatformService;
import cn.qihangerp.api.service.ShopService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.TableDataInfo;
import cn.qihangerp.api.service.ErpLogisticsCompanyService;
import java.util.List;

/**
 * 店铺Controller
 * 
 * @author qihang
 * @date 2023-12-31
 */
@AllArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopController extends BaseController {
    private final ErpLogisticsCompanyService erpLogisticsCompanyService;
    private final ShopService shopService;
    private final ErpShopPlatformService shopPlatformService;

    /**
     * 查询店铺列表logistics
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:list')")
    @GetMapping("/list")
    public TableDataInfo list(Shop shop) {
        List<Shop> list = shopService.selectShopList(shop);
        return getDataTable(list);
    }

    /**
     * 获取店铺详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:query')")
    @GetMapping(value = "/shop/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(shopService.getById(id));
    }

    /**
     * 新增店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:add')")
    @Log(title = "店铺管理", businessType = BusinessType.INSERT)
    @PostMapping("/shop")
    public AjaxResult add(@RequestBody Shop shop)
    {
        shop.setId(null);
        shop.setModifyOn(System.currentTimeMillis()/1000);
        return toAjax(shopService.save(shop));
    }

    /**
     * 修改店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:edit')")
    @Log(title = "店铺管理", businessType = BusinessType.UPDATE)
    @PutMapping("/shop")
    public AjaxResult edit(@RequestBody Shop shop)
    {
        Shop shopInfo = shopService.getById(shop.getId());
        if (shopInfo == null) {
            return AjaxResult.error("店铺不存在");
        }
        shop.setModifyOn(System.currentTimeMillis() / 1000);
        return toAjax(shopService.updateById(shop));
    }


    /**
     * 删除店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:remove')")
    @Log(title = "店铺管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/shop/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        Shop shopInfo = shopService.getById(id);
        if (shopInfo == null) {
            return AjaxResult.error("店铺不存在");
        }
        return toAjax(shopService.removeById(id));
    }

//    /**
//     * 查询店铺列表logistics
//     */
//    @GetMapping("/logistics")
//    public TableDataInfo logisticsList(Integer platform, Integer shopId, PageQuery pageQuery)
//    {
//        PageResult<SysLogisticsCompany> result = logisticsCompanyService.queryPageList(platform, shopId, pageQuery);
//        return getDataTable(result);
//    }

    /**
     * 查询店铺平台列表
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:list')")
    @GetMapping("/platformList")
    public TableDataInfo platformList() {
        List<ErpShopPlatform> list = shopPlatformService.selectPlatformList();
        return getDataTable(list);
    }

    /**
     * 获取店铺平台详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:query')")
    @GetMapping("/platform/{id}")
    public AjaxResult getPlatform(@PathVariable("id") Long id) {
        ErpShopPlatform platform = shopPlatformService.getById(id);
        if (platform == null) {
            return AjaxResult.error("平台不存在");
        }
        return success(platform);
    }

    /**
     * 修改店铺平台配置
     */
    @PreAuthorize("@ss.hasPermi('shop:shop:edit')")
    @Log(title = "店铺平台", businessType = BusinessType.UPDATE)
    @PutMapping("/platform")
    public AjaxResult updatePlatform(@RequestBody ErpShopPlatform platform) {
        if (platform.getId() == null) {
            return AjaxResult.error("缺少平台ID");
        }
        if (shopPlatformService.getById(platform.getId()) == null) {
            return AjaxResult.error("平台不存在");
        }
        return toAjax(shopPlatformService.updateById(platform));
    }
}
