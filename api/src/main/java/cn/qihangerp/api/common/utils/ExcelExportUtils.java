package cn.qihangerp.api.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Excel 导出工具（前端 download() 以 POST + blob 方式调用）
 *
 * @author qihang
 */
public class ExcelExportUtils {

    private ExcelExportUtils() {
    }

    /**
     * 写出 xlsx 文件到响应流
     *
     * @param response  响应对象
     * @param fileName  文件名（不含扩展名）
     * @param sheetName 工作表名
     * @param headers   表头
     * @param rows      数据行
     */
    public static void write(HttpServletResponse response, String fileName, String sheetName,
                             List<String> headers, List<List<Object>> rows) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encoded + ".xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headStyle = workbook.createCellStyle();
            Font bold = workbook.createFont();
            bold.setBold(true);
            headStyle.setFont(bold);

            Sheet sheet = workbook.createSheet(sheetName);
            Row head = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = head.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headStyle);
            }
            int rowIndex = 1;
            for (List<Object> row : rows) {
                Row r = sheet.createRow(rowIndex++);
                for (int i = 0; i < row.size(); i++) {
                    writeCell(r, i, row.get(i));
                }
            }
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(response.getOutputStream());
        }
    }

    private static void writeCell(Row row, int index, Object value) {
        Cell cell = row.createCell(index);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
        } else if (value instanceof Number n) {
            cell.setCellValue(n.doubleValue());
        } else if (value instanceof Date d) {
            cell.setCellValue(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", d));
        } else if (value instanceof Boolean b) {
            cell.setCellValue(b ? "是" : "否");
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
