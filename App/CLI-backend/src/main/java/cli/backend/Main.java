package cli.backend;

import cli.backend.handlers.AppHandler;

public class Main {
    public static void main(String[] args) {

        AppHandler app = AppHandler.getInstance();

        app.run();
    }
}
