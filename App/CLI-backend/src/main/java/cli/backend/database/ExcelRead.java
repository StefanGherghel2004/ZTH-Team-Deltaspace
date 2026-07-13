package cli.backend.database;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class ExcelRead {
    private final String filename;

    public ExcelRead(String filename) {
        this.filename = filename;
    }

    public void readExcel()  {
        System.out.println("Attempting to open Excel");
        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i=0;i<=sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                for(int j=0;j<row.getLastCellNum();j++){
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell);
                    System.out.printf("%-16s", cellValue);
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();

        }
    }
}

