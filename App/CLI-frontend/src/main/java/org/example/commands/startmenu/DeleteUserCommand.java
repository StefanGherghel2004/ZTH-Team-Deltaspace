package org.example.commands.startmenu;

import org.example.User;
import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class DeleteUserCommand implements Command {

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();
        UserApiClient userApiClient = UserApiClient.getInstance();

        User currentUser= appHandler.getCurrentUser();

        boolean confirmation = console.getUserConfirmation(
                "Are you sure you want to delete your account? (yes/no): ");
        if(confirmation){
            boolean removed = userApiClient.deleteUser(currentUser.getUsername(),currentUser.getToken());
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
