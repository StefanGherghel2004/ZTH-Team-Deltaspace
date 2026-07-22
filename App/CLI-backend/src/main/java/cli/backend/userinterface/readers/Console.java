package cli.backend.userinterface.readers;

import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.textformatters.Color;
import cli.backend.userinterface.textformatters.Theme;

import java.util.List;

public class Console {
    private static Console instance;
    private ConsoleReader reader;

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




}
