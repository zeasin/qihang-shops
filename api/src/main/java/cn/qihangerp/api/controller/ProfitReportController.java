package cn.qihangerp.api.controller;

import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.domain.vo.CompletenessVo;
import cn.qihangerp.api.domain.vo.LinkProfitVo;
import cn.qihangerp.api.domain.vo.OrderProfitVo;
import cn.qihangerp.api.domain.vo.ShopProfitVo;
import cn.qihangerp.api.service.ProfitEngineService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 利润分析（订单/链接/店铺三层级报表 + 完整度 + 导出）
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/profit")
public class ProfitReportController extends BaseController {
    private final ProfitEngineService profitEngineService;

    private String monthOrDefault(String month) {
        return StringUtils.hasText(month) ? month : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    /**
     * 订单利润（一行一单）
     */
    @GetMapping("/order")
    public AjaxResult order(String month, Long shopId) {
        List<OrderProfitVo> rows = profitEngineService.orderProfit(monthOrDefault(month), shopId);
        return AjaxResult.success(rows);
    }

    /**
     * 链接利润（一行一款，推广费分摊后）
     */
    @GetMapping("/link")
    public AjaxResult link(String month, Long shopId) {
        List<LinkProfitVo> rows = profitEngineService.linkProfit(monthOrDefault(month), shopId);
        return AjaxResult.success(rows);
    }

    /**
     * 店铺利润（一行一店，净利润）
     */
    @GetMapping("/shop")
    public AjaxResult shop(String month, Long shopId) {
        List<ShopProfitVo> rows = profitEngineService.shopProfit(monthOrDefault(month), shopId);
        return AjaxResult.success(rows);
    }

    /**
     * 数据完整度检查
     */
    @GetMapping("/completeness")
    public AjaxResult completeness(String month) {
        CompletenessVo vo = profitEngineService.completeness(monthOrDefault(month));
        return AjaxResult.success(vo);
    }

    /**
     * 导出 Excel（level: order|link|shop；前端 download 走 POST 表单，同时兼容 GET）
     */
    @RequestMapping(value = "/export", method = { RequestMethod.GET, RequestMethod.POST })
    public void export(HttpServletResponse response, String level, String month, Long shopId) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String m = monthOrDefault(month);
        String lvl = StringUtils.hasText(level) ? level : "shop";
        String fileName = URLEncoder.encode("利润报表-" + lvl + "-" + m, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headStyle = workbook.createCellStyle();
            Font bold = workbook.createFont();
            bold.setBold(true);
            headStyle.setFont(bold);

            Sheet sheet = workbook.createSheet("利润报表");
            switch (lvl) {
                case "order" -> exportOrder(sheet, headStyle, profitEngineService.orderProfit(m, shopId));
                case "link" -> exportLink(sheet, headStyle, profitEngineService.linkProfit(m, shopId));
                default -> exportShop(sheet, headStyle, profitEngineService.shopProfit(m, shopId));
            }
            workbook.write(response.getOutputStream());
        }
    }

    private void header(Sheet sheet, CellStyle style, String... titles) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(style);
        }
    }

    private void cell(Row row, int i, Object v) {
        Cell cell = row.createCell(i);
        if (v instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
        } else if (v instanceof Number n) {
            cell.setCellValue(n.doubleValue());
        } else {
            cell.setCellValue(v == null ? "" : v.toString());
        }
    }

    private void exportOrder(Sheet sheet, CellStyle style, List<OrderProfitVo> rows) {
        header(sheet, style, "订单号", "店铺", "实收", "商品成本", "快递", "平台扣费", "扣费来源", "售后成本", "订单利润", "利润率%");
        int r = 1;
        for (OrderProfitVo vo : rows) {
            Row row = sheet.createRow(r++);
            cell(row, 0, vo.getOrderNum());
            cell(row, 1, vo.getShopName());
            cell(row, 2, vo.getPayAmount());
            cell(row, 3, vo.getGoodsCost());
            cell(row, 4, vo.getExpressFee());
            cell(row, 5, vo.getPlatformFee());
            cell(row, 6, "bill".equals(vo.getPlatformFeeSource()) ? "对账单" : "订单参考");
            cell(row, 7, vo.getAfterSaleCost());
            cell(row, 8, vo.getProfit());
            cell(row, 9, vo.getProfitRate());
        }
    }

    private void exportLink(Sheet sheet, CellStyle style, List<LinkProfitVo> rows) {
        header(sheet, style, "链接名称", "销量", "实收", "商品成本", "快递", "平台扣费", "售后成本", "分摊推广费", "链接贡献利润", "利润率%");
        int r = 1;
        for (LinkProfitVo vo : rows) {
            Row row = sheet.createRow(r++);
            cell(row, 0, vo.getGoodsName());
            cell(row, 1, vo.getSalesQty());
            cell(row, 2, vo.getPayAmount());
            cell(row, 3, vo.getGoodsCost());
            cell(row, 4, vo.getExpressFee());
            cell(row, 5, vo.getPlatformFee());
            cell(row, 6, vo.getAfterSaleCost());
            cell(row, 7, vo.getPromotionFee());
            cell(row, 8, vo.getProfit());
            cell(row, 9, vo.getProfitRate());
        }
    }

    private void exportShop(Sheet sheet, CellStyle style, List<ShopProfitVo> rows) {
        header(sheet, style, "店铺", "实收", "商品成本", "快递", "平台扣费", "售后成本", "推广费", "固定费用", "店铺净利润", "利润率%");
        int r = 1;
        for (ShopProfitVo vo : rows) {
            Row row = sheet.createRow(r++);
            cell(row, 0, vo.getShopName());
            cell(row, 1, vo.getPayAmount());
            cell(row, 2, vo.getGoodsCost());
            cell(row, 3, vo.getExpressFee());
            cell(row, 4, vo.getPlatformFee());
            cell(row, 5, vo.getAfterSaleCost());
            cell(row, 6, vo.getPromotionFee());
            cell(row, 7, vo.getFixedFee());
            cell(row, 8, vo.getNetProfit());
            cell(row, 9, vo.getProfitRate());
        }
    }
}
