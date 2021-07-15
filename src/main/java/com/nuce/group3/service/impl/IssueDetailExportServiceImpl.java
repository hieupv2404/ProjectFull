package com.nuce.group3.service.impl;

import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import com.nuce.group3.service.IssueDetailExportService;
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
public class IssueDetailExportServiceImpl implements IssueDetailExportService {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    @Override
    public ByteArrayResource exportReport(List<IssueDetailResponse> issueDetails) throws IOException {
        try {
            log.info("Create Report");
            writeHeaderLine();
            writeDetailLines(issueDetails);
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
        sheet = workbook.createSheet("Issue Detail Report");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Issue Detail", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        font.setFontHeightInPoints((short) (10));
        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        createCell(row, 0, "#", style);
        createCell(row, 1, "Issue Code", style);
        createCell(row, 2, "Product", style);
        createCell(row, 3, "Imei", style);
        createCell(row, 4, "Price", style);

    }

    public void writeDetailLines(List<IssueDetailResponse> issueDetails) {
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        style.setFont(font);
        int index = 1;
        for (IssueDetailResponse issueDetail : issueDetails) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, index++, style);
            createCell(row, columnCount++, issueDetail.getIssueCode(), style);
            createCell(row, columnCount++, issueDetail.getProductName(), style);
            createCell(row, columnCount++, issueDetail.getImei(), style);
            createCell(row, columnCount++, issueDetail.getPrice(), style);
        }
        for (int i = 0; i <= 4; i++) {
            if (i == 0) {
                sheet.setColumnWidth(i, 4000);
            } else {
                sheet.setColumnWidth(i, 6000);
            }
        }
    }
}
