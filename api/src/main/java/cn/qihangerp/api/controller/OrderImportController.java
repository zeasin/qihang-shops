package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.*;
import cn.qihangerp.api.domain.ErpImportTemplate;
import cn.qihangerp.api.mapper.ErpImportTemplateMapper;
import cn.qihangerp.api.request.ManualOrderBo;
import cn.qihangerp.api.service.OrderImportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单导入（Excel 表头映射）+ 手动建单 + 映射模板
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/orderImport")
public class OrderImportController extends BaseController {
    private final OrderImportService orderImportService;
    private final ErpImportTemplateMapper templateMapper;

    /**
     * 第一步：解析 Excel，返回表头/行数/样例（前端据此做映射）
     */
    @PostMapping("/parse")
    public AjaxResult parse(@RequestPart("file") MultipartFile file) {
        try {
            return AjaxResult.success(orderImportService.parse(file));
        } catch (Exception e) {
            log.error("订单 Excel 解析失败", e);
            return AjaxResult.error("文件解析失败，请使用 .xlsx/.xls 格式");
        }
    }

    /**
     * 第三步：按映射预览（前 50 行 + 重复/未匹配统计），不入库
     */
    @PostMapping("/preview")
    public AjaxResult preview(@RequestPart("file") MultipartFile file,
                              @RequestParam Long shopId,
                              @RequestParam String mapping) {
        try {
            return AjaxResult.success(orderImportService.preview(shopId, mapping, file));
        } catch (Exception e) {
            log.error("订单导入预览失败", e);
            return AjaxResult.error(e.getMessage() == null ? "预览失败" : e.getMessage());
        }
    }

    /**
     * 第四步：提交入库
     */
    @PostMapping("/commit")
    public AjaxResult commit(@RequestPart("file") MultipartFile file,
                             @RequestParam Long shopId,
                             @RequestParam String mapping) {
        try {
            return AjaxResult.success(orderImportService.commit(getUsername(), shopId, mapping, file));
        } catch (Exception e) {
            log.error("订单导入入库失败", e);
            return AjaxResult.error(e.getMessage() == null ? "导入失败" : e.getMessage());
        }
    }

    /**
     * 手动建单
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody ManualOrderBo bo) {
        ResultVo<Long> result = orderImportService.createManual(getUsername(), bo);
        return result.getCode() == ResultVoEnum.SUCCESS.getIndex()
                ? AjaxResult.success("建单成功", result.getData())
                : AjaxResult.error(result.getMsg());
    }

    // ==================== 映射模板 ====================

    @GetMapping("/template/list")
    public AjaxResult templateList(Integer platform) {
        LambdaQueryWrapper<ErpImportTemplate> wrapper = new LambdaQueryWrapper<ErpImportTemplate>()
                .eq(platform != null, ErpImportTemplate::getPlatform, platform)
                .orderByDesc(ErpImportTemplate::getId);
        List<ErpImportTemplate> list = templateMapper.selectList(wrapper);
        return AjaxResult.success(list);
    }

    @PostMapping("/template/save")
    public AjaxResult templateSave(@RequestBody ErpImportTemplate template) {
        if (template.getPlatform() == null || !StringUtils.hasText(template.getTemplateName())
                || !StringUtils.hasText(template.getMappingJson())) {
            return AjaxResult.error("请填写模板名称并完成映射");
        }
        if (template.getId() != null) {
            ErpImportTemplate exists = templateMapper.selectById(template.getId());
            if (exists == null) {
                return AjaxResult.error("模板不存在");
            }
            ErpImportTemplate update = new ErpImportTemplate();
            update.setId(template.getId());
            update.setTemplateName(template.getTemplateName());
            update.setMappingJson(template.getMappingJson());
            update.setUpdateBy(getUsername());
            update.setUpdateTime(new Date());
            templateMapper.updateById(update);
            return AjaxResult.success("模板已更新");
        }
        template.setCreateBy(getUsername());
        template.setCreateTime(new Date());
        templateMapper.insert(template);
        return AjaxResult.success("模板已保存");
    }

    @DeleteMapping("/template/del/{ids}")
    public AjaxResult templateDel(@PathVariable Long[] ids) {
        return toAjax(templateMapper.delete(new LambdaQueryWrapper<ErpImportTemplate>()
                .in(ErpImportTemplate::getId, Arrays.stream(ids).toList())
                ));
    }
}
