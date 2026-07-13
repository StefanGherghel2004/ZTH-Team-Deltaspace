package cli.backend.readers;

import cli.backend.textformatters.Color;

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

    public String getStringInput(String prompt) {
        System.out.println(prompt);
        return reader.readString();
    }

    public int getIntInput(String prompt) {
        System.out.println(prompt);
        return reader.readInt();
    }

    public int getIntInRangeInput(int min, int max, String prompt) {
        System.out.println(prompt);
        return reader.readIntInRange(min, max);
    }

    public boolean getUserConfirmation(String prompt) {
        System.out.println(prompt);
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




}
