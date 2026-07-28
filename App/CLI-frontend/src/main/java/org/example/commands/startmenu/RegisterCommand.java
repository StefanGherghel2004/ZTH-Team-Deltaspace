package org.example.commands.startmenu;


import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.userinterface.readers.Console;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        Console console = Console.getInstance();
        RestClient restClient = RestClient.create();
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

            restClient.post()
                    .uri("http://localhost:8080/api/users/addUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            console.success("Registration successful! Welcome to our platform.");
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}