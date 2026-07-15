package cli.backend.commands.startmenu;

import cli.backend.User;
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

        User currentUser= appHandler.getCurrentUser();

        boolean confirmation = console.getUserConfirmation("Are you sure you want to delete your account? (yes/no): ");
        if(confirmation){
            boolean removed=userService.deleteUser(currentUser );
            if(removed){
                appHandler.setCurrentState(AppHandler.State.NOT_LOGGED_IN);
                appHandler.setCurrentUser(null);
                console.success("User account deleted successfully!");
                return true;
            }
        }
        else{
            console.info("User deletion cancelled.");
            appHandler.setCurrentState(AppHandler.State.LOGGED_IN);
        }
        return true;
    }
}
