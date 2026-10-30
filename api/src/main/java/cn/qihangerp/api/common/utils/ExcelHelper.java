package cn.qihangerp.api.common.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 读取工具（导入场景：.xlsx/.xls 统一处理，单元格统一转字符串）
 */
public class ExcelHelper {

    /**
     * 读取第一个 Sheet 的全部行，每行按列转为字符串（跳过全空行）
     */
    public static List<List<String>> readRows(InputStream in) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return rows;
            }
            int lastCol = 0;
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                lastCol = Math.max(lastCol, row.getLastCellNum());
            }
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                List<String> cells = new ArrayList<>();
                boolean hasValue = false;
                for (int c = 0; c < lastCol; c++) {
                    String v = cellString(row.getCell(c));
                    cells.add(v);
                    if (v != null && !v.trim().isEmpty()) {
                        hasValue = true;
                    }
                }
                if (hasValue) {
                    rows.add(cells);
                }
            }
        }
        return rows;
    }

    public static String cellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue() == null ? null : cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(DateUtil.getJavaDate(cell.getNumericCellValue()));
            }
            return NumberToTextConverter.toText(cell.getNumericCellValue()).trim();
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception e) {
                try {
                    return NumberToTextConverter.toText(cell.getNumericCellValue()).trim();
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return null;
    }

    public static String get(List<String> row, int index) {
        if (row == null || index >= row.size()) {
            return null;
        }
        String v = row.get(index);
        return v == null ? null : v.trim();
    }
}
