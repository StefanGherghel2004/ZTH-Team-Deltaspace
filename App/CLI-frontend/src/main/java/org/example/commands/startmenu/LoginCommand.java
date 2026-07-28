package org.example.commands.startmenu;

import org.example.User;
import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class LoginCommand implements Command {

    private record AuthResponse(String token, User user) {}

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        RestClient restClient = RestClient.create();

        console.info("Welcome to the login page.");
        try {
            String username = console.getStringInput("Insert your username or email:");
            String password = console.getStringInput("Insert your password:");

            try {
                AuthResponse response = restClient
                        .post()
                        .uri("http://localhost:8080/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("usernameOrEmail", username, "password", password))
                        .retrieve()
                        .body(AuthResponse.class);

                if (response != null) {
                    String token = response.token();
                    Console.getInstance().error(token);
                    User user = response.user();

                    app.setJwtToken(token);
                    app.setCurrentUser(user);
                    app.setCurrentState(AppHandler.State.LOGGED_IN);
                    console.info("Successfully logged in as " + user.getUsername() + "!");
                    return true;
                }

            } catch (HttpClientErrorException.Unauthorized e) {
                console.error("Login failed: Invalid username/email or password.");
            } catch (Exception e) {
                console.error("Login error: " + e.getMessage());
            }
        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}