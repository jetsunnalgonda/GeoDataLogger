package com.isikhaluk.datalogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelLogData
{
    public XSSFWorkbook workbook;
//    public XSSFSheet sheet;
    public XSSFSheet[] sheets = new XSSFSheet[0];
//    public static FileInputStream fi;
    public FileOutputStream outputStream;
    public Row row;
    public Cell cell;
    public String fileName = "GeoDataLog";
    public String fileSuffix = "xlsx";
    public String filePattern; // = "(GeoDataLog)(?:\\((\\d+)\\))?(\\.xlsx)";
    public Pattern PATTERN;
    public int[] rowCount = {0, 0, 0, 0, 0, 0, 0, 0};
    public ArrayList<Object> storage = new ArrayList<>();

    public ExcelLogData() throws IOException {
        filePattern = "(" + fileName + ")(?:\\((\\d+)\\))?(\\." + fileSuffix + ")";
        System.out.println(filePattern);
        PATTERN = Pattern.compile(filePattern);
        fileName = getNewName(fileName + "." + fileSuffix);
        System.out.println(fileName);

        workbook = new XSSFWorkbook();
    }

    public void createLog(int sheetNo, String sheetName, String xAxisTitle, String yAxisTitle) {
        do {
            sheets = Arrays.copyOf(sheets, sheets.length+1);
        } while (sheets.length<sheetNo+1);
//        XSSFSheet[] copySheets = new XSSFSheet[sheetNo+1];
//        sheets = Arrays.copyOf(copySheets, copySheets.length);
        sheets[sheetNo] = workbook.createSheet(sheetName);

        // Create the styles
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create the headers
        String[] columns = {xAxisTitle, yAxisTitle};
        Row headerRow = sheets[sheetNo].createRow(0);
        rowCount[sheetNo]++;
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
            sheets[sheetNo].autoSizeColumn(i);
        }

        // Open a new file to write
//        try {
//            outputStream = new FileOutputStream(fileName);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
    }
//    public void writeAndSaveLog(Object[] listOfStuff) {
//        try {
//            outputStream = new FileOutputStream(fileName);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        row = sheet.createRow(rowCount);
//        rowCount++;
//
//        int index = 0;
//        for(Object stuff:listOfStuff) {
//            if (stuff instanceof Number) {
////                row.createCell(index).setCellValue(Float.parseFloat(String.valueOf(stuff)));
//                row.createCell(index++).setCellValue((Float) stuff);
//            } else if (stuff instanceof String) {
//                row.createCell(index++).setCellValue((String) stuff);
//            }
//        }
//
//        try {
//            workbook.write(outputStream);
//            outputStream.close();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }

    public void writeToObject(Object[] listOfStuff) {
        storage.add(listOfStuff);
//        System.out.println("storage: " + storage);
    }
    public void writeLog(int sheetNo, Object[] listOfStuff) {
        row = sheets[sheetNo].createRow(rowCount[sheetNo]);
        rowCount[sheetNo]++;
        int index = 0;
        for(Object stuff:listOfStuff) {
            if (stuff instanceof Float) {
//                row.createCell(index).setCellValue(Float.parseFloat(String.valueOf(stuff)));
                row.createCell(index++).setCellValue((Float) stuff);
            } else if (stuff instanceof Double) {
                row.createCell(index++).setCellValue((Double) stuff);
            } else if (stuff instanceof Integer) {
                row.createCell(index++).setCellValue((Integer) stuff);
            } else if (stuff instanceof String) {
                row.createCell(index++).setCellValue((String) stuff);
            }
        }


    }
//    public void writeLogOld() {
//        for (int i = 0; i < storage.size(); i++) {
//            row = sheet.createRow(rowCount);
//            rowCount++;
//            int index = 0;
//            Object[] listOfStuff = (Object[]) storage.get(i);
//            for(Object stuff:listOfStuff) {
//                if (stuff instanceof Number) {
////                row.createCell(index).setCellValue(Float.parseFloat(String.valueOf(stuff)));
//                    row.createCell(index++).setCellValue((Float) stuff);
//                } else if (stuff instanceof String) {
//                    row.createCell(index++).setCellValue((String) stuff);
//                }
//            }
//        }
//
//    }
    public void saveLog() {
        if (workbook == null) return;
        try {
            outputStream = new FileOutputStream(fileName);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        try {
            workbook.write(outputStream);
//            System.out.println("Writing to Excel file");
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }
    public void closeLog() {
        if (workbook == null) return;
        try {
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }

    private String getNewName(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            Matcher m = PATTERN.matcher(filename);
            if (m.matches()) {
                String prefix = m.group(1);
                String last = m.group(2);
                String suffix = m.group(3);

                if (suffix == null) suffix = "";

                int count = last != null ? Integer.parseInt(last) : 0;

                do {
                    count++;
                    filename = prefix + "(" + count + ")" + suffix;
                    file = new File(filename);
                } while (file.exists());
            }
        }
        return filename;
    }
}