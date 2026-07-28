package org.example.commands.mainmenu;

import org.example.User;
import org.example.apiclients.UserApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

import java.util.HashMap;
import java.util.Map;

public class EditUserCommand implements Command {
    String editType;

    public EditUserCommand(String editType) {
        this.editType = editType != null ? editType : "";
    }

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();
        UserApiClient userApiClient = UserApiClient.getInstance();

        User userToEdit = appHandler.getCurrentUser();
        String currentUsername = userToEdit.getUsername();

        // Create a dynamic map holding ONLY the modified field
        Map<String, Object> payload = new HashMap<>();

        try {
            switch (editType) {
                case "email" -> {
                    String newEmail = console.getValidEmailInput();
                    payload.put("email", newEmail);
                }

                case "password" -> {
                    String newPassword = console.getValidPasswordInput();
                    payload.put("password", newPassword);
                }

                case "dateOfBirth" -> {
                    String newDateOfBirth = console.getValidDateOfBirthInput();
                    payload.put("dateOfBirth", newDateOfBirth);
                }

                default -> {
                    console.error("Invalid edit operation: " + editType);
                    appHandler.setCurrentState(AppHandler.State.EDIT_USER);
                    return true;
                }
            }

            // Only send fields that were actually changed!
            User updatedUser = userApiClient.updateUser(currentUsername, payload, appHandler.getJwtToken());

            if (updatedUser != null) {
                appHandler.setCurrentUser(updatedUser); // Update local user state with fresh DB data
                console.success("User updated successfully!");
            }

            appHandler.setCurrentState(AppHandler.State.EDIT_USER);
        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}