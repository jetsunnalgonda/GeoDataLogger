package com.isikhaluk.datalogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelWriteDemo
{
    public static XSSFWorkbook wb;
    public static XSSFSheet sheet;
    public static FileInputStream fi;
    public static FileOutputStream fo;
    public static Row row;
    public static Cell cell;
    public static String fileName = "GeoDataLog";
    public static String fileSuffix = "xlsx";
    public static String filePattern; // = "(GeoDataLog)(?:\\((\\d+)\\))?(\\.xlsx)";
    public static Pattern PATTERN;

    public static void main(String[] args) throws Exception
    {
//        String patternText = "\\(" + fileName + "\\)\\(?:\\(\\(\\d+\\)\\)\\)?\\(\\." + fileExtension + "\\)";
        filePattern = "(" + fileName + ")(?:\\((\\d+)\\))?(\\." + fileSuffix + ")";
//        fileName = "(GeoDataLog)(?:\\((\\d+)\\))?(\\.xlsx)";
        System.out.println(filePattern);
        PATTERN = Pattern.compile(filePattern);
        fileName = getNewName(fileName + "." + fileSuffix);
        System.out.println(fileName);


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Java Books");

        Object[][] bookData = {
                {"Head First Java", "Kathy Serria", 79},
                {"Effective Java", "Joshua Bloch", 36},
                {"Clean Code", "Robert martin", 42},
                {"Thinking in Java", "Bruce Eckel", 35},
        };

        int rowCount = 0;

        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;

            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }


        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        }





//        File file = new File("data.xlsx");
//        Boolean created = file.createNewFile();
//        System.out.println("File created: " + created);

//        try {
//        File file = new File(fileName);

//        file.createNewFile();
//        fi = new FileInputStream(file);
//        System.out.println("fi = " + fi);
//        } catch (IOException e) {
//            System.out.println("Cannot open file to write");
//            System.out.println(e);
//        }

//        wb = new XSSFWorkbook();
//        sheet = wb.createSheet("Data Log");

//        XSSFFont headerFont = wb.createFont();
//        headerFont.setBold(true);
//        headerFont.setFontHeightInPoints((short) 14);
//        headerFont.setColor(IndexedColors.RED.getIndex());
//
//        CellStyle headerCellStyle = wb.createCellStyle();
//        headerCellStyle.setFont(headerFont);

        // Create the headers
//        String[] columns = {"Time (s)", "Force (kN)"};
//        Row headerRow = sheet.createRow(0);
//        for (int i = 0; i < columns.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(columns[i]);
//            cell.setCellStyle(headerCellStyle);
//        }
        // Write random data
//        int rowNum = 1;
//
//        for (int j = 0; j < 10; j++) {
//            Row row = sheet.createRow(rowNum++);
//            row.createCell(0).setCellValue(Math.random() * (500 - 50 + 1) + 50);
//            row.createCell(1).setCellValue("Second one");
//        }

//        File file = new File(fileName);
//        FileOutputStream outputStream = new FileOutputStream(fileName);
//        fo = new FileOutputStream(file);
//        wb.write(fo);
//        fo.close();

// Resize all columns to fit the content size
//        for (int i = 0; i < columns.length; i++) {
//            sheet.autoSizeColumn(i);
//        }

//        wb= WorkbookFactory.create(fi);
//        System.out.println("wb: " + wb);
//        s=wb.getSheet("sheet");
//        //create one row
//        r=s.createRow(1);
//        c=r.createCell(0);
//        c.setCellValue("Raj");
//        System.out.println(c.getStringCellValue());
//        fo=new FileOutputStream(fileName);
//        wb.write(fo);
//        fo.flush();
//        fo.close();
//        System.out.println("Data entered in a Data excel file");
    }
    static String getNewName(String filename) {
//        System.out.println("Hello");
        File file = new File(filename);
        if (file.exists()) {
//            System.out.println("File exists");
            Matcher m = PATTERN.matcher(filename);
            if (m.matches()) {
                String prefix = m.group(1);
                String last = m.group(2);
                String suffix = m.group(3);
//                System.out.println("prefix: " + prefix);
//                System.out.println("last: " + last);
//                System.out.println("suffix: " + suffix);

                if (suffix == null) suffix = "";

                int count = last != null ? Integer.parseInt(last) : 0;

                do {
                    count++;
//                    System.out.println("count = " + count);
                    filename = prefix + "(" + count + ")" + suffix;
                    file = new File(filename);
                } while (file.exists());
            }
        }
        return filename;
    }
}