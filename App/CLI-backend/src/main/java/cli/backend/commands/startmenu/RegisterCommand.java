package cli.backend.commands.startmenu;


import cli.backend.commands.Command;
import cli.backend.exceptions.BackNavigationException;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;



public class RegisterCommand implements Command {
    @Override
    public boolean execute() {
        Console console = Console.getInstance();
        UserService userService = UserService.getInstance();

        console.info("Welcome to the registration page.");
        try {
            String username = console.getValidUsernameInput();

            String email = console.getValidEmailInput();

            String password = console.getValidPasswordInput();

            String dateOfBirth = console.getValidDateOfBirthInput();

            password = PasswordService.hash(password);
            userService.addUser(username, email, password, dateOfBirth);
            console.success("Registration successful! Welcome to our platform.");
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}