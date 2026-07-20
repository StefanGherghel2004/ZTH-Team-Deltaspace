package cli.backend.database;
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

    public void setupDatabases () throws IOException {

        if (excelWrite.isSheetEmpty(excelWrite.userDatabasePath))
            excelWrite.write(excelWrite.userDatabasePath,
                    List.of("ID","Username","E-mail","Password","Date of Birth"));

        if (excelWrite.isSheetEmpty(excelWrite.postDatabasePath))
            excelWrite.write(excelWrite.postDatabasePath,
                    List.of("ID","User","Title","Contents","Image link","Community","NSFW"));

        if (excelWrite.isSheetEmpty(excelWrite.communityDatabasePath))
            excelWrite.write(excelWrite.communityDatabasePath,
                    List.of("Name","Topic","Description","Community creator"));
    }
}
