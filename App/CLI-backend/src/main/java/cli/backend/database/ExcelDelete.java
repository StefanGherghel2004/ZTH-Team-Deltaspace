package cli.backend.database;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelDelete {

    private static ExcelDelete instance;
    private ExcelDelete() {}

    public static ExcelDelete getInstance() {
        if (instance == null) {
            instance = new ExcelDelete();
        }
        return instance;
    }

    public boolean deleteRowfromExcel(String filename, int columnToSearch, String valueToDelete) {

        File excelFile = new File(filename);

        if (!excelFile.exists()) {

            return false;
        }

        try (FileInputStream fileInput = new FileInputStream(excelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fileInput)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            int rowIndexToDelete = -1;
            int lastRow = sheet.getLastRowNum();


            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Cell cell = row.getCell(columnToSearch, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = formatter.formatCellValue(cell).trim();



                if (cellValue.equalsIgnoreCase(valueToDelete.trim())) {
                    rowIndexToDelete = i;

                    break;
                }
            }


            if (rowIndexToDelete != -1) {
                Row rowToDelete = sheet.getRow(rowIndexToDelete);
                if (rowToDelete != null) {
                    sheet.removeRow(rowToDelete);
                }

                if (rowIndexToDelete < lastRow) {

                    sheet.shiftRows(rowIndexToDelete + 1, lastRow, -1);
                }



                try (FileOutputStream fileOutput = new FileOutputStream(excelFile)) {
                    workbook.write(fileOutput);
                    fileOutput.flush();
                }


                return true;
            }

        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
        return false;
    }
}