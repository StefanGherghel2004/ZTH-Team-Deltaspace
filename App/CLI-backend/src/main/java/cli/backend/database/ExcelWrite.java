package cli.backend.database;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dhatim.fastexcel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelWrite {

    private static ExcelWrite instance = null;

    private ExcelWrite () {

    }

    public static ExcelWrite getInstance() {

        if (instance == null)
            instance = new ExcelWrite();

        return instance;
    }

    public static void write(String path, List<String> entry) {
        try {
            File file = DatabaseInitialize.getInstance().createFile(path);
            XSSFWorkbook workbook;

            if (file.length() == 0) {
                workbook = new XSSFWorkbook();
            } else {
                try (FileInputStream in = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(in);
                }
            }

            try {
                XSSFSheet sheet = workbook.getNumberOfSheets() > 0 ?
                        workbook.getSheetAt(0) : workbook.createSheet();

                int nextRowIndex = sheet.getLastRowNum() + 1;
                if (sheet.getPhysicalNumberOfRows() == 0) {
                    nextRowIndex = 0;
                }

                Row row = sheet.createRow(nextRowIndex);

                int celNum = 0;
                for (String data : entry) {
                    Cell cell = row.createCell(celNum);
                    cell.setCellValue(data);
                    sheet.autoSizeColumn(celNum);
                    celNum++;
                }

                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }
            } finally {
                workbook.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
