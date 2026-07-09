package cli.backend.commands.startmenu;


import cli.backend.commands.Command;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;

public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        ConsoleReader consoleReader = ConsoleReader.getInstance();
        UserService userService = UserService.getInstance();

        System.out.println("Welcome to the registration page.");

        System.out.println("Please enter your username (4-20 characters, alphanumeric):");
        String username;
        while (!userService.validateUsername(username = consoleReader.readString())){
            System.out.println("Invalid username format. Please try again.");
        }

        System.out.println("Please enter your email address:");
        String email;
        while (!userService.validateEmail(email = consoleReader.readString())) {
            System.out.println("Invalid email format. Must be like 'user@domain.com'.");
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