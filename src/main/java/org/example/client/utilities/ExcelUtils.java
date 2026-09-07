package org.example.client.utilities;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelUtils() {}

    /**
     * Resolves a file path relative to project root or testdata directory.
     */
    public static String getFilePath(String fileName) {
        File directFile = new File(fileName);
        if (directFile.isAbsolute() && directFile.exists()) {
            return fileName;
        }
        return Paths.get(System.getProperty("user.dir"), "testdata", fileName).toAbsolutePath().toString();
    }

    /**
     * Reads an entire sheet into a 2D Object array. Ideal for TestNG @DataProvider.
     * Skips the header row (row index 0).
     */
    public static Object[][] getSheetDataAsArray(String filePath, String sheetName) {
        String resolvedPath = getFilePath(filePath);
        validateFileExists(resolvedPath);

        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = getValidSheet(workbook, sheetName);
            int totalRows = sheet.getLastRowNum(); // excludes 0 if empty
            if (totalRows <= 0) {
                return new Object[0][0];
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalStateException("Header row (row 0) is missing in sheet: " + sheetName);
            }

            int totalCols = headerRow.getLastCellNum();
            Object[][] data = new Object[totalRows][totalCols];

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < totalCols; j++) {
                    if (row == null) {
                        data[i - 1][j] = "";
                    } else {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        data[i - 1][j] = FORMATTER.formatCellValue(cell).trim();
                    }
                }
            }
            return data;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + resolvedPath + " | Error: " + e.getMessage(), e);
        }
    }

    /**
     * Reads sheet data as a list of Maps, where each Map key is the column header name.
     * E.g.: List.get(0).get("Username") -> "testuser"
     */
    public static List<Map<String, String>> getSheetDataAsListOfMap(String filePath, String sheetName) {
        String resolvedPath = getFilePath(filePath);
        validateFileExists(resolvedPath);

        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = getValidSheet(workbook, sheetName);
            int totalRows = sheet.getLastRowNum();
            if (totalRows <= 0) {
                return dataList;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalStateException("Header row (row 0) is missing in sheet: " + sheetName);
            }

            int totalCols = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int j = 0; j < totalCols; j++) {
                Cell cell = headerRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                headers.add(FORMATTER.formatCellValue(cell).trim());
            }

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, totalCols)) {
                    continue; // Skip blank rows
                }

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < totalCols; j++) {
                    String headerName = headers.get(j);
                    if (!headerName.isEmpty()) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowMap.put(headerName, FORMATTER.formatCellValue(cell).trim());
                    }
                }
                dataList.add(rowMap);
            }

            return dataList;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file into Map: " + resolvedPath + " | Error: " + e.getMessage(), e);
        }
    }

    /**
     * Reads a single specific cell value.
     */
    public static String getCellData(String filePath, String sheetName, int rowNum, int colNum) {
        String resolvedPath = getFilePath(filePath);
        validateFileExists(resolvedPath);

        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = getValidSheet(workbook, sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }

            Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            return FORMATTER.formatCellValue(cell).trim();

        } catch (IOException e) {
            throw new RuntimeException("Error reading cell [" + rowNum + "," + colNum + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Writes or updates a cell value in an existing Excel file (e.g. Test Status "PASS" / "FAIL").
     */
    public static void setCellData(String filePath, String sheetName, int rowNum, int colNum, String value) {
        String resolvedPath = getFilePath(filePath);
        validateFileExists(resolvedPath);

        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }

            Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellValue(value != null ? value : "");

            // Write back changes
            try (FileOutputStream fos = new FileOutputStream(resolvedPath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to write to Excel cell [" + rowNum + "," + colNum + "]: " + e.getMessage(), e);
        }
    }

    // --- Private Helper Methods ---

    private static void validateFileExists(String path) {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("Excel file does not exist at path: " + path);
        }
    }

    private static Sheet getValidSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet named '" + sheetName + "' does not exist in workbook.");
        }
        return sheet;
    }

    private static boolean isRowEmpty(Row row, int totalCols) {
        for (int c = 0; c < totalCols; c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !FORMATTER.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}