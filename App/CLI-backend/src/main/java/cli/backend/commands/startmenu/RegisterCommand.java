package cli.backend.commands.startmenu;


import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.database.ExcelRead;
import cli.backend.database.ExcelWrite;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;

import java.util.List;



public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        ConsoleReader consoleReader = ConsoleReader.getInstance();
        UserService userService = UserService.getInstance();
        ExcelWrite excelWrite = ExcelWrite.getInstance();
        ExcelRead excelRead = ExcelRead.getInstance();

        System.out.println("Welcome to the registration page.");

        System.out.println("Please enter your username (4-20 characters, alphanumeric):");
        String username;
        while(true) {

            if (!userService.validateUsername(username = consoleReader.readString())) {
                System.out.println("Invalid username format. Please try again.");
                continue;
            }

            if (excelRead.checkDuplicateCell(username, 1,"App/CLI-backend/databases/UserDatabase.xlsx")) {
                System.out.println("Username already exists. Please choose a different username.");
                continue;
            }
            break;
        }

        System.out.println("Please enter your email address:");
        String email;
        while(true){
        if(!userService.validateEmail(email = consoleReader.readString())) {
            System.out.println("Invalid email format. Must be like 'user@domain.com'.");
            continue;
        }
        if(excelRead.checkDuplicateCell(email,2,"App/CLI-backend/databases/UserDatabase.xlsx")){
            System.out.println("Email already exists. Please use a different email address.");
            continue;
            }
            break;
        }

        System.out.println("Please enter your password (min 8 chars, 1 uppercase, 1 lowercase, 1 number):");
        String password;
        while (!userService.validatePassword(password = consoleReader.readString())) {
            System.out.println("Invalid password format. Please ensure it meets the requirements.");
        }

        System.out.println("Please enter your date of birth (DD-MM-YYYY): ");
        String dateOfBirth;
        while (!userService.validateDateOfBirth(dateOfBirth = consoleReader.readString())) {
            System.out.println("Invalid date of birth format. Ensure the format is correct (e.g., 15-08-2010) and that you are at least 13 years old.");
        }

        password = PasswordService.hash(password);
        userService.addUser(username, email, password, dateOfBirth);
        System.out.println("Registration successful! Welcome to our platform.");
        return true;
    }
}