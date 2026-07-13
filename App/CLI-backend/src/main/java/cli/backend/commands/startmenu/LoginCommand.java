package cli.backend.commands.startmenu;

import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.exceptions.InvalidUserAccountException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.UserService;

public class LoginCommand implements Command {
    @Override
    public boolean execute() {
        UserService userService = UserService.getInstance();
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        console.info("Welcome to the login page.");
        while (true) {
            String username = console.getStringInput("Insert your username or email:");
            String password = console.getStringInput("Insert your password:");

            try {
                User user = userService.validateUserAccount(username, password);
                console.success("Successfully logged in - " + user.getUsername());
                app.setCurrentUser(user);
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                return true;
            } catch (InvalidUserAccountException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
