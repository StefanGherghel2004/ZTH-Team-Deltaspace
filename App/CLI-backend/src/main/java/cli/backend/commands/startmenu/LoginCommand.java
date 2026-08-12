package cli.backend.commands.startmenu;

import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.exceptions.BackNavigationException;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.Logger;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.UserService;

public class LoginCommand implements Command {
    @Override
    public boolean execute() {
        UserService userService = UserService.getInstance();
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        console.info("Welcome to the login page.");
        try {
            String username = console.getStringInput("Insert your username or email:");
            String password = console.getStringInput("Insert your password:");

            try {
                User user = userService.validateUserAccount(username, password);
                console.success("Successfully logged in - " + user.getUsername());
                app.setCurrentUser(user);
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                return true;
            } catch (Exception e) {
                Logger.severe("Login failed: " + e.getMessage());
                console.error(e.getMessage());
            }
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }

        return true;
    }
}
