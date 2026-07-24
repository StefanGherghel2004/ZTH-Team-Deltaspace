package cli.backend.commands.mainmenu;

import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.duplicates.CheckDuplicate;
import cli.backend.duplicates.UserDuplicate;
import cli.backend.handlers.AppHandler;
import cli.backend.repositories.UserRepository;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;
import cli.backend.userinterface.readers.Console;

import java.sql.Date;
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
        UserService userService = UserService.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        CheckDuplicate userCheck = new UserDuplicate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        User userToEdit = app.getCurrentUser();

        switch (editType){
            case "username" -> {
                String newUsername;
                while(true) {
                    newUsername = console.getStringInput("Please enter your new username (4-20 characters, alphanumeric):");

                    if (!userService.validateUsername(newUsername)) {
                        console.error("Invalid username format. Please try again.");
                        continue;
                    }

                    if (userCheck.isDuplicate(newUsername)) {
                        console.error("Username already exists. Please choose a different username.");
                        continue;
                    }
                    break;
                }
                userToEdit.setUsername(newUsername);
            }

            case "email" -> {
                String newEmail;
                while(true){
                    newEmail = console.getStringInput("Please enter your email address:");
                    if(!userService.validateEmail(newEmail)) {
                        console.error("Invalid email format. Must be like 'user@domain.com'.");
                        continue;
                    }
                    if(userCheck.isDuplicate(newEmail)) {
                        console.error("Email already exists. Please use a different email address.");
                        continue;
                    }
                    break;
                }
                userToEdit.setEmail(newEmail);
            }

            case "password" -> {
                String newPassword;
                while (true) {
                    newPassword = console.getStringInput("Please enter your password (min 8 chars, 1 uppercase, 1 lowercase, 1 number):");
                    if(userService.validatePassword(newPassword)) {
                        break;
                    }
                    console.error("Invalid password format. Please ensure it meets the requirements.");
                }
                userToEdit.setPassword(PasswordService.hash(newPassword));
            }

            case "dateOfBirth" -> {
                String newDateOfBirth;
                while (true) {
                    newDateOfBirth = console.getStringInput("Please enter your date of birth (DD-MM-YYYY): ");
                    if (userService.validateDateOfBirth(newDateOfBirth)) {
                        break;
                    }
                    console.error("Invalid date of birth format. Ensure the format is correct (e.g., 15-08-2010) and that you are at least 13 years old.");
                }
                userToEdit.setDateOfBirth(LocalDate.parse(newDateOfBirth, formatter));
            }

            default -> {
                console.error("Invalid edit operation: " + editType);
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                return true;
            }
        }

        boolean updated = userRepository.updateUser(userToEdit);

        if (updated) {
            console.success("User updated successfully!");
        } else {
            console.error("Failed to update user in database.");
        }

        app.setCurrentState(AppHandler.State.LOGGED_IN);
        return true;
    }
}
