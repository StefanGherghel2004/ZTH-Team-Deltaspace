package org.example.commands.mainmenu;

import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class LogoutCommand implements Command {
    @Override
    public boolean execute() {
        Console.getInstance().info("Logging out...");
        AppHandler app = AppHandler.getInstance();
        app.setCurrentUser(null);
        app.setCurrentState(AppHandler.State.NOT_LOGGED_IN);
        return true;
    }
}