package cli.backend.duplicates;

import cli.backend.database.ExcelRead;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class UserDuplicate implements  CheckDuplicate{
    private static String filePath= "App/CLI-backend/databases/UserDatabase.xlsx";
    private static final int userColumn=1;
    ExcelRead excelRead= ExcelRead.getInstance();
    @Override
    public boolean isDuplicate(String userToCheck) {
        if(userToCheck==null){
            return false;
        }
        List<String> existingUsers=excelRead.getColumnValues(filePath,userColumn);
        for (String u:existingUsers){
            if(u.equalsIgnoreCase(userToCheck)){
                return true;
            }
        }
        return false;
    }
}
