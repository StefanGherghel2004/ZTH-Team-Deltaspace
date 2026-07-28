package org.example.commands.mainmenu;

import org.example.User;
import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EditUserCommand implements Command {
    String editType;

    public EditUserCommand(String editType){
        this.editType = editType != null ? editType : "";
    }
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        UserApiClient userApiClient = UserApiClient.getInstance();

        User userToEdit = app.getCurrentUser();
        String oldUsername = userToEdit.getUsername();
        try {
            switch (editType) {
                case "username" -> {
                    String newUsername = console.getValidUsernameInput();
                    userToEdit.setUsername(newUsername);
                }

                case "email" -> {
                    String newEmail = console.getValidEmailInput();
                    userToEdit.setEmail(newEmail);
                }

                case "password" -> {
                    String newPassword = console.getValidPasswordInput();
                    userToEdit.setPassword(newPassword);
                }

                case "dateOfBirth" -> {
                    String newDateOfBirth = console.getValidDateOfBirthInput();
                    userToEdit.setDateOfBirth(newDateOfBirth);
                }

                default -> {
                    console.error("Invalid edit operation: " + editType);
                    app.setCurrentState(AppHandler.State.EDIT_USER);
                    return true;
                }
            }
            Map<String,Object> payload = Map.of(
                    "username",userToEdit.getUsername(),
                    "email",userToEdit.getEmail(),
                    "password",userToEdit.getPassword(),
                    "dateOfBirth",userToEdit.getDateOfBirth()
            );
            userApiClient.updateUser(oldUsername,payload);
            console.success("User updated successfully!");
            app.setCurrentState(AppHandler.State.EDIT_USER);
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}
