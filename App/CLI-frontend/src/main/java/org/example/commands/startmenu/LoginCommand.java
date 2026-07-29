package org.example.commands.startmenu;

import org.example.User;
import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.web.client.HttpClientErrorException;

public class LoginCommand implements Command {

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        UserApiClient userApiClient = UserApiClient.getInstance();

        console.info("Welcome to the login page.");
        try {
            String username = console.getStringInput("Insert your username or email:");
            String password = console.getStringInput("Insert your password:");

            try {
                UserApiClient.AuthResponse response = userApiClient.login(username, password);

                if (response != null && response.token() != null) {

                    if (response.user().isDeleted()) {
                        console.error("This account is deleted");
                        return true;
                    }

                    app.setJwtToken(response.token());
                    app.setCurrentUser(response.user());
                    app.setCurrentState(AppHandler.State.LOGGED_IN);

                    console.success("Successfully logged in as " + response.user().getUsername() + "!");
                    return true;
                } else {
                    console.error("Login failed: Invalid server response.");
                }

            } catch (HttpClientErrorException.Unauthorized e) {
                console.error("Login failed: Invalid username/email or password");
            } catch (Exception e) {
                console.error("Login error: " + e.getMessage());
            }
        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}