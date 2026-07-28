package org.example;

import org.example.handlers.AppHandler;
import org.example.loggers.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        AppHandler app = AppHandler.getInstance();

        Logger.init();
        app.run();
    }
}
