package cli.backend.commands.startmenu;

import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.exceptions.InvalidUserAccountException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.UserService;

public class LoginCommand implements Command {
    @Override
    public boolean execute() {
        ConsoleReader reader = ConsoleReader.getInstance();
        UserService userService = UserService.getInstance();
        AppHandler app = AppHandler.getInstance();

        System.out.println("Welcome to the login page.");
        while (true) {
            System.out.println("Insert your username or email:");
            String loginUsername = reader.readString();

            System.out.println("Insert your password:");
            String loginPassword = reader.readString();

            try {
                User user = userService.validateUserAccount(loginUsername, loginPassword);
                System.out.println("Successfully logged in - " + user.getUsername());
                app.setCurrentUser(user);
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                return true;
            } catch (InvalidUserAccountException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
