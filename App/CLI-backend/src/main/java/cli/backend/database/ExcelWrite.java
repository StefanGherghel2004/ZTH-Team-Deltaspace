package cli.backend.database;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ExcelWrite {

    private static ExcelWrite instance = null;
    private static XSSFWorkbook workbook;
    private static XSSFSheet sheet;
    private static Row row;
    private static Cell cell;

    public String userDatabasePath = "App/CLI-backend/databases/UserDatabase.xlsx";
    public String postDatabasePath = "App/CLI-backend/databases/PostDatabase.xlsx";
    public String communityDatabasePath = "App/CLI-backend/databases/CommunityDatabase.xlsx";

    private ExcelWrite () {

    }

    public static ExcelWrite getInstance() {

        if (instance == null)
            instance = new ExcelWrite();

        return instance;
    }

    public void write(String path, List<String> entry) {
        try {
            File file = DatabaseInitialize.getInstance().createFile(path);

            if (file.length() == 0) {
                workbook = new XSSFWorkbook();
            } else {
                try (FileInputStream in = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(in);
                }
            }

            try {
                sheet = workbook.getNumberOfSheets() > 0 ?
                        workbook.getSheetAt(0) : workbook.createSheet();

                int nextRowIndex = sheet.getLastRowNum() + 1;
                if (sheet.getPhysicalNumberOfRows() == 0) {
                    nextRowIndex = 0;
                }

                row = sheet.createRow(nextRowIndex);

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

    public int getNumberOfEntries (String path) {
        try{
            workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            sheet = workbook.getSheetAt(0);
            return sheet.getLastRowNum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
