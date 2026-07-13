package cli.backend.database;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

public class DatabaseInitialize {

    private static DatabaseInitialize instance = null;

    private DatabaseInitialize () {}

    public static DatabaseInitialize getInstance() {

        if (instance == null)
            instance = new DatabaseInitialize();

        return instance;
    }

    public void initialize () throws IOException {

        File userDatabase = createFile("App/CLI-backend/databases/UserDatabase.xlsx");
        File postDatabase = createFile("App/CLI-backend/databases/PostDatabase.xlsx");
        File communityDatabase = createFile("App/CLI-backend/databases/CommunityDatabase.xlsx");
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
}
