package cli.backend.database;
import org.apache.poi.ss.usermodel.*;
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

    public String userDatabasePath = "Mihai/CLI-backend/databases/UserDatabase.xlsx";
    public String postDatabasePath = "Mihai/CLI-backend/databases/PostDatabase.xlsx";
    public String communityDatabasePath = "Mihai/CLI-backend/databases/CommunityDatabase.xlsx";

    private ExcelWrite () {

    }

    public static ExcelWrite getInstance() {

        if (instance == null)
            instance = new ExcelWrite();

        return instance;
    }

    public File prepareFile(String path) {
        File file = new File(path);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        return file;
    }

    public void write(String path, List<String> entry) {
        try {
            File file = prepareFile(path);

            if (!file.exists() || file.length() == 0) {
                workbook = new XSSFWorkbook();
            } else {
                try (FileInputStream in = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(in);
                }
            }

            try {
                workbook = rowEntry(workbook,entry);

                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }
            } finally {

                workbook.close();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to write to Excel file at: " + path, e);
        }
    }

    public int getNumberOfEntries (String path) {
        try{
            workbook = new XSSFWorkbook(new FileInputStream(path));
            sheet = workbook.getSheetAt(0);
            return sheet.getLastRowNum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSheetEmpty(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return true;
        }

        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis)) {

            if (xssfWorkbook.getNumberOfSheets() == 0) {
                return true;
            }

            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            if (xssfSheet == null || xssfSheet.getPhysicalNumberOfRows() == 0) {
                return true;
            }

            for (int r = 0; r <= xssfSheet.getLastRowNum(); r++) {
                Row xssfRow = xssfSheet.getRow(r);
                if (xssfRow == null) {
                    continue;
                }
                for (int c = 0; c < xssfRow.getLastCellNum(); c++) {
                    Cell cell = xssfRow.getCell(c);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        if (cell.getCellType() == CellType.STRING) {
                            if (!cell.getStringCellValue().trim().isEmpty()) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    public static int getCurrentId(String filepath) {
        File file = new File(filepath);
        int currentId;
        if (!file.exists() || file.length() == 0) {
            currentId = 0;
            return currentId;
        }

        try (FileInputStream in = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            if (workbook.getNumberOfSheets() == 0) {
                currentId = 0;
                return currentId;
            }
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastRowIndex = sheet.getLastRowNum();

            if (lastRowIndex == 0) {
                currentId = 0;
                return currentId;
            }

            Row row = sheet.getRow(lastRowIndex);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = cell.toString().trim();
                    if (cellValue.endsWith(".0")) {
                        cellValue = cellValue.substring(0, cellValue.length() - 2);
                    }
                    currentId = Integer.parseInt(cellValue);
                    return currentId;
                }
            }
            currentId = 0;
            return currentId;
        } catch (IOException | NumberFormatException e) {
            currentId = 0;
            return currentId;
        }
    }

    public XSSFWorkbook rowEntry(XSSFWorkbook workbook, List<String> entry) {

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
        return workbook;
    }
}
