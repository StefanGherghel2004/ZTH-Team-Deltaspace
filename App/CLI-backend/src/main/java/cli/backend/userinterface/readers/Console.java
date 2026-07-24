package cli.backend.userinterface.readers;

import cli.backend.duplicates.CheckDuplicate;
import cli.backend.duplicates.UserDuplicate;
import cli.backend.handlers.AppHandler;
import cli.backend.services.UserService;
import cli.backend.userinterface.textformatters.Color;
import cli.backend.userinterface.textformatters.Theme;

import java.util.List;

public class Console {
    private static Console instance;
    private ConsoleReader reader;
    UserService userService = UserService.getInstance();
    CheckDuplicate userCheck = new UserDuplicate();

    private Console() {
        this.reader = ConsoleReader.getInstance();
    }

    public static Console getInstance() {
        if (instance == null) {
            instance = new Console();
        }

        return instance;
    }

    private void printPromptPrefix() {
        AppHandler app = AppHandler.getInstance();
        StringBuilder prefix = new StringBuilder();

        if (app.getCurrentUser() != null) {
            prefix.append(Theme.formatUsername(app.getCurrentUser().getUsername()));
        }

        prefix.append(Color.textCyan(Theme.PROMPT));

        System.out.print(prefix);
    }

    public String getMultiLineInput(String prompt) {
        System.out.println(prompt);
        System.out.println(Color.textCyan("(Type your text. Press Enter for a new line. Type ':done' on an empty line to finish)"));

        return reader.readMultiLine(false);
    }

    public String getStringInput(String prompt) {
        System.out.println(prompt);
        printPromptPrefix();
        return reader.readString();
    }

    public String getStringInput(String prompt, boolean allowEmpty) {
        System.out.println(prompt);
        printPromptPrefix();
        return reader.readString(allowEmpty);
    }

    public int getIntInput(String prompt) {
        System.out.println(prompt);
        printPromptPrefix();
        return reader.readInt();
    }

    public Long getLongInput(String prompt) {
        System.out.println(prompt);
        printPromptPrefix();
        return reader.readLong();
    }

    public int getIntInRangeInput(int min, int max, String prompt) {
        System.out.println(prompt);
        printPromptPrefix();
        return reader.readIntInRange(min, max);
    }

    public boolean getUserConfirmation(String prompt) {
        System.out.println(prompt);
        printPromptPrefix();
        String confirm = reader.readString();

        return confirm.equalsIgnoreCase("yes");
    }

    public void success(String message) {
        System.out.println(Color.textGreen(message));
    }

    public void error(String message) {
        System.out.println(Color.textRed(message));
    }

    public void info(String message) {
        System.out.println(message);
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void printIndexList(String title, List<String> content) {
        info(title);
        for (String i : content) {
            info((content.indexOf(i) + 1) + ". " + i);
        }
    }

    public String getValidUsernameInput () {
        String username;
        while(true) {
            username = getStringInput("Please enter your username (4-20 characters, alphanumeric):");

            if (!userService.validateUsername(username)) {
                error("Invalid username format. Please try again.");
                continue;
            }

            if (userCheck.isDuplicate(username)) {
                error("Username already exists. Please choose a different username.");
                continue;
            }
            return username;
        }
    }

    public String getValidEmailInput () {
        String email;
        while(true){
            email = getStringInput("Please enter your email address:");
            if(!userService.validateEmail(email)) {
                error("Invalid email format. Must be like 'user@domain.com'.");
                continue;
            }
            if(userCheck.isDuplicate(email)) {
                error("Email already exists. Please use a different email address.");
                continue;
            }

            return email;
        }
    }

    public String getValidPasswordInput () {
        String password;
        while (true) {
            password = getStringInput("Please enter your password (min 8 chars, 1 uppercase, 1 lowercase, 1 number):");
            if(userService.validatePassword(password)) {
                return password;
            }
            error("Invalid password format. Please ensure it meets the requirements.");
        }
    }

    public String getValidDateOfBirthInput () {
        String dateOfBirth;
        while (true) {
            dateOfBirth = getStringInput("Please enter your date of birth (DD-MM-YYYY): ");
            if (userService.validateDateOfBirth(dateOfBirth)) {
                return dateOfBirth;
            }
            error("Invalid date of birth format. Ensure the format is correct (e.g., 15-08-2010) and that you are at least 13 years old.");
        }
    }
}
