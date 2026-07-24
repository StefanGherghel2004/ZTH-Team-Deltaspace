package cli.backend.commands.mainmenu;

import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.repositories.UserRepository;
import cli.backend.services.PasswordService;
import cli.backend.userinterface.readers.Console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditUserCommand implements Command {
    String editType;

    public EditUserCommand(String editType){
        this.editType = editType != null ? editType : "";
    }

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        User userToEdit = app.getCurrentUser();

        switch (editType){
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
                userToEdit.setPassword(PasswordService.hash(newPassword));
            }

            case "dateOfBirth" -> {
                String newDateOfBirth = console.getValidDateOfBirthInput();
                userToEdit.setDateOfBirth(LocalDate.parse(newDateOfBirth, formatter));
            }

            default -> {
                console.error("Invalid edit operation: " + editType);
                app.setCurrentState(AppHandler.State.EDIT_USER);
                return true;
            }
        }

        boolean updated = userRepository.updateUser(userToEdit);

        if (updated) {
            console.success("User updated successfully!");
        } else {
            console.error("Failed to update user in database.");
        }

        app.setCurrentState(AppHandler.State.EDIT_USER);
        return true;
    }
}
