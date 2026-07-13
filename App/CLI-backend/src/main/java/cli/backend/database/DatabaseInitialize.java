package cli.backend.database;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DatabaseInitialize {

    private static DatabaseInitialize instance = null;
    private ExcelWrite excelWrite = ExcelWrite.getInstance();

    private DatabaseInitialize () throws IOException {

    }

    public static DatabaseInitialize getInstance() throws IOException {

        if (instance == null)
            instance = new DatabaseInitialize();

        return instance;
    }

    public File createFile(String path) throws IOException {
        File file = new File(path);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public void setupDatabases () throws IOException {

        excelWrite.write(excelWrite.userDatabasePath,
                List.of("ID","Username","E-mail","Password","Date of Birth"));

        excelWrite.write(excelWrite.postDatabasePath,
                List.of("ID","Title","Contents","Image link","Community","NSFW"));

        excelWrite.write(excelWrite.communityDatabasePath,
                List.of("Name","Topic","Description","Community creator"));
    }
}
