package cli.backend.commands.startmenu;

import cli.backend.commands.Command;
import cli.backend.duplicates.CheckDuplicate;
import cli.backend.duplicates.EmailDuplicate;
import cli.backend.duplicates.UserDuplicate;
import cli.backend.readers.Console;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;



public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        Console console = Console.getInstance();
        UserService userService = UserService.getInstance();
        CheckDuplicate userCheck = new UserDuplicate();
        CheckDuplicate emailCheck=new EmailDuplicate();


        console.info("Welcome to the registration page.");

        String username;
        while(true) {

            username = console.getStringInput("Please enter your username (4-20 characters, alphanumeric):");

            if (!userService.validateUsername(username)) {
                console.error("Invalid username format. Please try again.");
                continue;
            }

            if (userCheck.isDuplicate(username)) {
                console.error("Username already exists. Please choose a different username.");
                continue;
            }
            break;
        }

        String email;
        while(true){
            email = console.getStringInput("Please enter your email address:");
        if(!userService.validateEmail(email)) {
            console.error("Invalid email format. Must be like 'user@domain.com'.");
            continue;
        }
        if(emailCheck.isDuplicate(email)) {
            console.error("Email already exists. Please use a different email address.");
            continue;
        }

            break;
        }

        String password;
        while (true) {
            password = console.getStringInput("Please enter your password (min 8 chars, 1 uppercase, 1 lowercase, 1 number):");
            if(userService.validatePassword(password)) {
                break;
            }
            console.error("Invalid password format. Please ensure it meets the requirements.");
        }

        String dateOfBirth;
        while (true) {
            dateOfBirth = console.getStringInput("Please enter your date of birth (DD-MM-YYYY): ");
            if (userService.validateDateOfBirth(dateOfBirth)) {
                break;
            }
            console.error("Invalid date of birth format. Ensure the format is correct (e.g., 15-08-2010) and that you are at least 13 years old.");
        }

        password = PasswordService.hash(password);
        userService.addUser(username, email, password, dateOfBirth);
        console.success("Registration successful! Welcome to our platform.");
        return true;
    }
}