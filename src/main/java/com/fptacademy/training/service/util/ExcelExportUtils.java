package com.fptacademy.training.service.util;

import com.fptacademy.training.domain.User;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ExcelExportUtils {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private List<User> userList;

    private ServletOutputStream outputStream = null;

    public ExcelExportUtils(List<User> userList) {
        this.userList = userList;
        workbook = new XSSFWorkbook();
    }

    private void createHeaderRow() {
        sheet = workbook.createSheet("User");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        Row row = sheet.createRow(0);
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Full Name", style);
        createCell(row, 1, "Email", style);
        createCell(row, 2, "Code", style);
        createCell(row, 3, "Password", style);
        createCell(row, 4, "Gender", style);
        createCell(row, 5, "Role", style);
        createCell(row, 6, "Is activated", style);
        createCell(row, 7, "Level", style);
        createCell(row, 8, "Status", style);
        createCell(row, 9, "Avatar", style);
        createCell(row, 10, "Birthday", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof LocalDate) {
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("mm/dd/yyyy"));
            cell = row.createCell(columnCount, CellType.FORMULA);
            cell.setCellFormula("DATE(" + ((LocalDate) value).getYear() + "," + ((LocalDate) value).getMonthValue() + "," + ((LocalDate) value).getDayOfMonth() + ")");
            cell.setCellValue(cell.getCellFormula());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeCustomerData() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (User u : userList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, u.getFullName() == null ? "" : u.getFullName(), style);
            createCell(row, columnCount++, u.getEmail() == null ? "" : u.getEmail(), style);
            createCell(row, columnCount++, u.getCode() == null ? "" : u.getCode(), style);
            createCell(row, columnCount++, u.getPassword() == null ? "" : u.getPassword(), style);
            createCell(row, columnCount++, u.getGender(), style);
            createCell(row, columnCount++, u.getRole() == null ? "" : u.getRole().getName(), style);
            createCell(row, columnCount++, u.getActivated(), style);
            createCell(row, columnCount++, u.getLevel() == null ? "" : u.getLevel().getName(), style);
            createCell(row, columnCount++, u.getStatus() == null ? "" : u.getStatus().toString(), style);
            createCell(row, columnCount++, u.getAvatarUrl() == null ? "" : u.getAvatarUrl(), style);
            createCell(row, columnCount, u.getBirthday(), style);
        }
    }

    public void exportDataToExcel(HttpServletResponse response) {
        createHeaderRow();
        writeCustomerData();
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
