package cli.backend;


import cli.backend.handlers.AppHandler;
import cli.backend.loggers.*;

public class Main {
    public static void main(String[] args) {

        AppHandler app = AppHandler.getInstance();

        Logger.init();
        app.run();
    }
}
