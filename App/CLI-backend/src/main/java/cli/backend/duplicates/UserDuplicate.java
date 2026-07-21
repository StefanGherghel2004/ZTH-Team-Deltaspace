package cli.backend.duplicates;

import cli.backend.database.ExcelRead;
import cli.backend.database.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class UserDuplicate implements  CheckDuplicate{

    @Override
    public boolean isDuplicate(String usernameOrEmail) {
        if(usernameOrEmail == null){
            return false;
        }

        return UserRepository.getInstance().findByUsernameOrEmail(usernameOrEmail).isPresent();
    }
}
