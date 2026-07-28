package org.example.commands.startmenu;


import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.response.DuplicateKeyErrorParser;
import org.example.exceptions.BackNavigationException;
import org.example.userinterface.readers.Console;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        Console console = Console.getInstance();
        RestClient restClient = RestClient.create();
        UserApiClient userApiClient = UserApiClient.getInstance();
        console.info("Welcome to the registration page.");
        try {
            String username = console.getValidUsernameInput();

            String email = console.getValidEmailInput();

            String password = console.getValidPasswordInput();

            String dateOfBirth = console.getValidDateOfBirthInput();

            Map<String,Object> payload = Map.of(
                    "username",username,
                    "email",email,
                    "password",password,
                    "dateOfBirth",dateOfBirth
            );

            userApiClient.registerUser(payload);
            console.success("Registration successful! Welcome to our platform.");
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        catch (HttpClientErrorException.Conflict e) {
            String friendlyError = DuplicateKeyErrorParser.extractDuplicateKeyError(e);
            console.error(friendlyError);

        } catch (HttpClientErrorException e) {
            console.error("Registration failed: " + e.getResponseBodyAsString());
        }
        return true;
    }
}