package cn.qihangerp.api.controller.sys;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.TableDataInfo;
import cn.qihangerp.api.common.utils.ExcelExportUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.SysDictData;
import cn.qihangerp.api.service.ISysDictDataService;
import cn.qihangerp.api.service.ISysDictTypeService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 数据字典信息
 * 
 * @author qihang
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return getDataTable(new ArrayList<>());
//        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }


    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 导出字典数据
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException
    {
        List<SysDictData> list = dictDataService.selectDictDataList(new SysDictData());
        List<List<Object>> rows = new ArrayList<>();
        for (SysDictData item : list) {
            rows.add(Arrays.asList(item.getDictCode(), item.getDictSort(), item.getDictLabel(), item.getDictValue(),
                    item.getDictType(), item.getListClass(), "Y".equals(item.getIsDefault()) ? "是" : "否",
                    "0".equals(item.getStatus()) ? "正常" : "停用"));
        }
        ExcelExportUtils.write(response, "字典数据", "字典数据",
                Arrays.asList("字典编码", "字典排序", "字典标签", "字典键值", "字典类型", "样式", "是否默认", "状态"), rows);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return success(data);
    }

    /**
     * 新增字典类型
     */

    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        dict.setCreateBy(getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        dict.setUpdateBy(getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
