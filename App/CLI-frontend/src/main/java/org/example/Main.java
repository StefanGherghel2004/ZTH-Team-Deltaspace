package org.example;

import org.example.handlers.AppHandler;
import org.example.loggers.Logger;

public class Main {
    static void main() {
        AppHandler app = AppHandler.getInstance();

        Logger.init();
        app.run();
    }
}
