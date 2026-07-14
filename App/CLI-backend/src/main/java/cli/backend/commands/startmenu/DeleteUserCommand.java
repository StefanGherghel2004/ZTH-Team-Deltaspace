package cli.backend.commands.startmenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.services.UserService;

public class DeleteUserCommand implements Command {

    @Override
    public boolean execute() {
        UserService userService = UserService.getInstance();
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();

        boolean confirmation = console.getUserConfirmation("Are you sure you want to delete your account? (yes/no): ");

        return false;
    }
}
