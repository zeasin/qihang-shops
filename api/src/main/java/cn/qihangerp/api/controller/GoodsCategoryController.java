package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.*;
import cn.qihangerp.api.common.utils.ExcelExportUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.ErpGoodsCategoryAttribute;
import cn.qihangerp.api.service.ErpGoodsCategoryAttributeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cn.qihangerp.api.domain.ErpGoods;
import cn.qihangerp.api.domain.ErpGoodsCategory;
import cn.qihangerp.api.domain.ErpGoodsCategoryAttributeValue;
import cn.qihangerp.api.service.ErpGoodsCategoryAttributeValueService;
import cn.qihangerp.api.service.ErpGoodsCategoryService;
import cn.qihangerp.api.service.ErpGoodsService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@RestController
@RequestMapping("/goods/category")
public class GoodsCategoryController extends BaseController {
    private final ErpGoodsCategoryService categoryService;
    private final ErpGoodsCategoryAttributeValueService attributeValueService;
    private final ErpGoodsCategoryAttributeService attributeService;

    @GetMapping("/list")
    public TableDataInfo list()
    {
        List<ErpGoodsCategory> list = categoryService.list();
        return getDataTable(list);
    }

    /**
     * 导出商品分类
     */
    @Log(title = "商品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException
    {
        List<ErpGoodsCategory> list = categoryService.list(
                new LambdaQueryWrapper<ErpGoodsCategory>().orderByAsc(ErpGoodsCategory::getSort));
        List<List<Object>> rows = new ArrayList<>();
        for (ErpGoodsCategory item : list) {
            rows.add(Arrays.asList(item.getId(), item.getNumber(), item.getName(), item.getParentId(),
                    item.getPath(), item.getSort(), item.getRemark(), item.getCreateTime()));
        }
        ExcelExportUtils.write(response, "商品分类", "商品分类",
                Arrays.asList("ID", "分类编码", "分类名称", "上级分类ID", "分类路径", "排序", "描述", "创建时间"), rows);
    }
    /**
     * 获取商品分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('goods:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(categoryService.getById(id));
    }

    /**
     * 新增商品分类
     */
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpGoodsCategory bo)
    {
        bo.setId(null);
        bo.setCreateBy("手动添加");
        bo.setCreateTime(new Date());
        // 添加分类的时候把最顶级的ID找出来
        if(bo.getParentId().equals("0")){
            bo.setTopId("0");
        }else{
            ErpGoodsCategory parent = categoryService.getById(bo.getParentId());

            if(parent!=null) {
                if(parent.getTopId().equals("0")){
                    bo.setTopId(parent.getId());
                }else{
                    bo.setTopId(parent.getTopId());
                }
            }
        }
        return toAjax(categoryService.save(bo));
    }

    /**
     * 修改商品分类
     */
    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpGoodsCategory erpGoodsCategory)
    {
        erpGoodsCategory.setCreateTime(null);
        erpGoodsCategory.setCreateBy(null);
        erpGoodsCategory.setUpdateBy("手动更新");
        erpGoodsCategory.setUpdateTime(new Date());
        return toAjax(categoryService.updateById(erpGoodsCategory));
    }

    /**
     * 删除商品分类
     */
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(categoryService.removeBatchByIds(Arrays.stream(ids).toList()));
    }

    @GetMapping("/attributeList/{id}")
    public TableDataInfo attributeList(@PathVariable("id") Long id)
    {
        List<ErpGoodsCategoryAttribute> list = attributeService.list(
                new LambdaQueryWrapper<ErpGoodsCategoryAttribute>()
                        .eq(ErpGoodsCategoryAttribute::getCategoryId,id));
        return getDataTable(list);
    }

    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping("/attribute")
    public AjaxResult addAttributeList(@RequestBody ErpGoodsCategoryAttribute bo)
    {
        if(StringUtils.isEmpty(bo.getCategoryId())){
            return AjaxResult.error("缺少参数categoryId");
        }
        bo.setId(null);
        if(bo.getType() == 1){
            // 不能超过三个规格
            List<ErpGoodsCategoryAttribute> list = attributeService.list(
                    new LambdaQueryWrapper<ErpGoodsCategoryAttribute>()
                            .eq(ErpGoodsCategoryAttribute::getCategoryId,bo.getCategoryId())
                            .eq(ErpGoodsCategoryAttribute::getType,1)
            );
            if(list!=null && list.size()==3){
                return AjaxResult.error("规格最多只能添加3个");
            }else{
                ErpGoodsCategoryAttribute codeHas = list.stream().filter(x -> x.getCode().equals(bo.getCode())).findFirst().orElse(null);
                if(codeHas!=null){
                    return AjaxResult.error(bo.getCode()+"规格已存在，不能重复添加");
                }
            }
        }
        return toAjax(attributeService.save(bo));
    }
    @GetMapping(value = "/attribute/{id}")
    public AjaxResult getAttributeInfo(@PathVariable("id") Long id)
    {
        return success(attributeService.getById(id));
    }

    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping("/attribute")
    public AjaxResult attributeEdit(@RequestBody ErpGoodsCategoryAttribute bo)
    {
        bo.setCategoryId(null);
        return toAjax(attributeService.updateById(bo));
    }

    /**
     * 删除商品分类属性
     */
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/attribute/{ids}")
    public AjaxResult attributeRemove(@PathVariable Long[] ids)
    {
        return toAjax(attributeService.removeBatchByIds(Arrays.stream(ids).toList()));
    }


    @GetMapping("/attr_value/{id}")
    public TableDataInfo attrValueList(@PathVariable("id") String id)
    {
        var list = attributeValueService.list(
                new LambdaQueryWrapper<ErpGoodsCategoryAttributeValue>()
                        .eq(ErpGoodsCategoryAttributeValue::getCategoryAttributeId,id)
        );

        return getDataTable(list);
    }
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping("/attributeValue")
    public AjaxResult attributeValueAdd(@RequestBody ErpGoodsCategoryAttributeValue bo)
    {

        if(StringUtils.isEmpty(bo.getCategoryAttributeId())){
            return AjaxResult.error("缺少参数CategoryAttributeId");
        }
        if(bo.getOrderNum() == null) bo.setOrderNum(0);
        if(StringUtils.isEmpty(bo.getSkuCode())) bo.setSkuCode("00");
        bo.setId(null);
        bo.setIsDelete(0);

        return toAjax(attributeValueService.save(bo));
    }
    @GetMapping(value = "/attributeValue/{id}")
    public AjaxResult getAttributeValueInfo(@PathVariable("id") Long id)
    {
        return success(attributeValueService.getById(id));
    }

    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping("/attributeValue")
    public AjaxResult attributeValueEdit(@RequestBody ErpGoodsCategoryAttributeValue bo)
    {
        bo.setCategoryAttributeId(null);
        return toAjax(attributeValueService.updateById(bo));
    }

    /**
     * 删除商品分类属性
     */
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/attributeValue/{ids}")
    public AjaxResult attributeValueRemove(@PathVariable Long[] ids)
    {
        return toAjax(attributeValueService.removeBatchByIds(Arrays.stream(ids).toList()));
    }

    /**
     * 根据分类id，获取他的所有规格属性值
     * @param id
     * @return
     */
    @GetMapping("/attr_value_by_category/{id}")
    public AjaxResult attrValueByCategoryId(@PathVariable("id") String id)
    {
        ErpGoodsCategory category = categoryService.getById(id);
        if(category!=null){
            Map<String, List<ErpGoodsCategoryAttributeValue>> resultMap = new HashMap<>();
            List<ErpGoodsCategoryAttribute> attributes = attributeService.list(new LambdaQueryWrapper<ErpGoodsCategoryAttribute>().eq(ErpGoodsCategoryAttribute::getCategoryId, id).eq(ErpGoodsCategoryAttribute::getType, 1));
            List<String> collect = attributes.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<ErpGoodsCategoryAttributeValue> values = new ArrayList<>();
            if(!collect.isEmpty()) {
                values = attributeValueService.list(
                        new LambdaQueryWrapper<ErpGoodsCategoryAttributeValue>()
                                .in(ErpGoodsCategoryAttributeValue::getCategoryAttributeId, collect)
                                .eq(ErpGoodsCategoryAttributeValue::getIsDelete, 0)
                );
            }
            Map<String, List<ErpGoodsCategoryAttributeValue>> collect1 = values.stream().collect(Collectors.groupingBy(x -> x.getCategoryAttributeId()));
            collect1.forEach((key,value)->{
                ErpGoodsCategoryAttribute erpGoodsCategoryAttribute = attributes.stream().filter(x -> x.getId().equals(key)).findFirst().orElse(null);
                if(erpGoodsCategoryAttribute!=null) {
                    resultMap.put(erpGoodsCategoryAttribute.getCode(),value);
                }
            });


            return AjaxResult.success(resultMap);
        }


        return AjaxResult.success();
    }
}
