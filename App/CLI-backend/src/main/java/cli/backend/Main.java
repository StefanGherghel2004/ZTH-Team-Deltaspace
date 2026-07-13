package cli.backend;

import cli.backend.database.DatabaseInitialize;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        AppHandler app = AppHandler.getInstance();

        DatabaseInitialize.getInstance().initialize();
        Logger.init();
        app.run();
    }
}
