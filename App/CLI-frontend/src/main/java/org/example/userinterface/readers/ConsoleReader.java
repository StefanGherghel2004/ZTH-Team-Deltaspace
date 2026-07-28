package org.example.userinterface.readers;

import org.example.exceptions.BackNavigationException;
import org.example.userinterface.textformatters.Color;

import java.util.Scanner;

public class ConsoleReader {

    public static final String MULTILINE_STOP_SYM = ":delta";
    private static final String INPUT_CURSOR = Color.textCyan("> ");
    private static final String BACK_SYM = ":back";

    private static final String ERR_INVALID_NUMBER = "Please enter a valid number.";
    private static final String ERR_EMPTY_INPUT = "Input cannot be empty. Try again.";
    private static final String ERR_OUT_OF_RANGE = Color.textRed("Invalid option. Please enter a number between %d and %d.");
    private static final String ERR_BACK_COMMAND = "You cannot use the back command in a menu please select a valid option.";
    private static final String PROMPT_RANGE = "Choose an option (%d-%d): ";


    private final Scanner scanner;
    private static ConsoleReader instance = null;
    private static final Console console = Console.getInstance();

    public ConsoleReader () {

        this.scanner = new Scanner(System.in);
    }

    public static ConsoleReader getInstance () {

        if (instance == null)
            instance = new ConsoleReader();

        return instance;
    }

    //Reads a string value and returns it only if it is an integer
    public int readInt () {

        while (true) {

            String inputInteger = scanner.nextLine().trim();
            if (BACK_SYM.equalsIgnoreCase(inputInteger))
                throw new BackNavigationException();

            try{
                return Integer.parseInt(inputInteger);
            } catch (NumberFormatException e) {
                console.error(ERR_INVALID_NUMBER);
            }
        }
    }

    public Long readLong () {

        while (true) {

            String inputLong = scanner.nextLine().trim();
            if (BACK_SYM.equalsIgnoreCase(inputLong))
                throw new BackNavigationException();

            try{
                return Long.parseLong(inputLong);
            } catch (NumberFormatException e) {
                console.error(ERR_INVALID_NUMBER);
            }
        }
    }

    //Reads a string value and returns it only if it is an integer and in a specific range
    public int readIntInRange (int min, int max) {

        while (true) {
            System.out.printf(PROMPT_RANGE, min, max);
            try {
                int value = this.readInt();
                if (value >= min && value <= max) {
                    return value;
                } else
                    System.out.printf((ERR_OUT_OF_RANGE) + "\n", min, max);
            } catch(BackNavigationException backNavigationException){
                console.error(ERR_BACK_COMMAND);
            }
        }
    }

    public String readString() {
        return readString(false);
    }

    public String readString(boolean allowEmpty) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (BACK_SYM.equalsIgnoreCase(input))
                throw new BackNavigationException();
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }
                console.error(ERR_EMPTY_INPUT);
        }
    }

    public String readMultiLine(boolean allowEmpty) {
        while (true) {
            StringBuilder content = new StringBuilder();

            while (true) {
                System.out.print(INPUT_CURSOR);
                String line = scanner.nextLine();
                if (BACK_SYM.equalsIgnoreCase(line))
                    throw new BackNavigationException();

                if (line.trim().equalsIgnoreCase(MULTILINE_STOP_SYM)) {
                    break;
                }

                content.append(line).append("\n");
            }

            String result = content.toString().trim();

            if (allowEmpty || !result.isEmpty()) {
                return result;
            }

            console.error(ERR_EMPTY_INPUT);
        }
    }
}
