package cli.backend;

import cli.backend.database.DatabaseInitialize;

import cli.backend.handlers.AppHandler;
import cli.backend.loggers.*;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        AppHandler app = AppHandler.getInstance();

        DatabaseInitialize.getInstance().setupDatabases();
        Logger.init();
        app.run();

    }
}
