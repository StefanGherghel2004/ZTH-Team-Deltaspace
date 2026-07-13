package cli.backend.readers;

import cli.backend.textformatters.Color;

import java.util.Scanner;

public class ConsoleReader {

    private final Scanner scanner;
    private static ConsoleReader instance = null;

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

            try{
                return Integer.parseInt(inputInteger);
            } catch (NumberFormatException e) {
                System.out.println(Color.textRed("Please enter a valid number."));
            }
        }
    }

    //Reads a string value and returns it only if it is an integer and in a specific range
    public int readIntInRange (int min, int max) {

        while (true) {
            System.out.print("Choose an option (" + min + "-" + max + "): ");
            int value = this.readInt();
            if (value >= min && value <= max) {
                return value;
            }
            else
                System.out.println(Color.textRed("Invalid option. Please enter a number between" +
                        " " + min + " and " + max + "."));
        }
    }

    public String readString() {
        return readString(false);
    }

    public String readString(boolean allowEmpty) {
        while (true) {
            String input = scanner.nextLine().trim();
                
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }

            System.out.println(Color.textRed("Input cannot be empty. Try again."));
        }
    }

}
