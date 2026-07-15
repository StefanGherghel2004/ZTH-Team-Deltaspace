package cli.backend.commands.mainmenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;

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