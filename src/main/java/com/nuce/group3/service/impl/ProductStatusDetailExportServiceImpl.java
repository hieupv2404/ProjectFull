package com.nuce.group3.service.impl;

import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import com.nuce.group3.service.ProductStatusDetailExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ProductStatusDetailExportServiceImpl implements ProductStatusDetailExportService {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    @Override
    public ByteArrayResource exportReport(List<ProductStatusDetailResponse> productStatusDetails) throws IOException {
        try {
            log.info("Create Report");
            writeHeaderLine();
            writeDetailLines(productStatusDetails);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                workbook.write(bos);
            } finally {
                bos.close();
            }
            byte[] excelFileAsBytes = bos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(excelFileAsBytes);
            workbook.close();
            bos.close();
            return resource;
        } catch (Exception ex) {
            ex.getMessage();
        }
        return null;
    }

    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        //sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(String.valueOf((BigDecimal) value));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(String.valueOf((ZonedDateTime) value));
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public void writeHeaderLine() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Product Status Detail Report");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Product Status Detail", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        font.setFontHeightInPoints((short) (10));
        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        createCell(row, 0, "#", style);
        createCell(row, 1, "ID", style);
        createCell(row, 2, "Status List Code", style);
        createCell(row, 3, "Product", style);
        createCell(row, 4, "Quantity", style);
        createCell(row, 5, "Price One", style);
        createCell(row, 6, "Price Total", style);
        createCell(row, 7, "Quantity Rest", style);

    }

    public void writeDetailLines(List<ProductStatusDetailResponse> productStatusDetails) {
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        style.setFont(font);
        int index = 1;
        for (ProductStatusDetailResponse productStatusDetail : productStatusDetails) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, index++, style);
            createCell(row, columnCount++, productStatusDetail.getId(), style);
            createCell(row, columnCount++, productStatusDetail.getProductStatusListCode(), style);
            createCell(row, columnCount++, productStatusDetail.getProductInfo(), style);
            createCell(row, columnCount++, productStatusDetail.getQty(), style);
            createCell(row, columnCount++, productStatusDetail.getPriceOne(), style);
            createCell(row, columnCount++, productStatusDetail.getPriceTotal(), style);
            createCell(row, columnCount++, productStatusDetail.getQtyRest(), style);
        }
        for (int i = 0; i <= 7; i++) {
            if (i == 2 || i == 3 || i == 5 || i == 6) {
                sheet.setColumnWidth(i, 6000);
            } else {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }
}
