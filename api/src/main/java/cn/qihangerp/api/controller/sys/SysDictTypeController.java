package cn.qihangerp.api.controller.sys;

import cn.qihangerp.api.common.security.SecurityUtils;
import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.utils.ExcelExportUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.TableDataInfo;
import cn.qihangerp.api.domain.SysDictType;
import cn.qihangerp.api.service.ISysDictTypeService;

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
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController
{
    @Autowired
    private ISysDictTypeService dictTypeService;


    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return getDataTable(new ArrayList<>());
//        startPage(false);
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }


    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        return success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 导出字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException
    {
        List<SysDictType> list = dictTypeService.selectDictTypeList(new SysDictType());
        List<List<Object>> rows = new ArrayList<>();
        for (SysDictType item : list) {
            rows.add(Arrays.asList(item.getDictId(), item.getDictName(), item.getDictType(),
                    "0".equals(item.getStatus()) ? "正常" : "停用"));
        }
        ExcelExportUtils.write(response, "字典类型", "字典类型",
                Arrays.asList("字典编号", "字典名称", "字典类型", "状态"), rows);
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictType dict)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictType dict)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        dictTypeService.deleteDictTypeByIds(dictIds);
        return success();
    }

    /**
     * 刷新字典缓存
     */
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        dictTypeService.resetDictCache();
        return success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        if (SecurityUtils.getLoginUser().getUserId() != 1) return AjaxResult.error("没有权限");
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return success(dictTypes);
    }
}
