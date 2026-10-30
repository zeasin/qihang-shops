package cn.qihangerp.api.service;

import cn.qihangerp.api.common.ResultVo;
import cn.qihangerp.api.request.ManualOrderBo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 订单导入（Excel 表头映射）+ 手动建单服务
 */
public interface OrderImportService {

    /**
     * 解析 Excel：返回表头、行数、样例行（不做任何入库）
     */
    Map<String, Object> parse(MultipartFile file) throws IOException;

    /**
     * 预览：按映射解析，返回前 50 行（重复订单/未匹配SKU打标）与统计，不入库
     *
     * @param mappingJson 系统字段→Excel列索引 的 JSON
     */
    Map<String, Object> preview(Long shopId, String mappingJson, MultipartFile file) throws IOException;

    /**
     * 提交入库：订单落 erp_order、明细落 erp_order_item；重复订单号跳过；未知SKU自动建档（成本留空）
     */
    Map<String, Object> commit(String userName, Long shopId, String mappingJson, MultipartFile file) throws IOException;

    /**
     * 手动建单（零星订单、补录历史单）
     */
    ResultVo<Long> createManual(String userName, ManualOrderBo bo);
}
