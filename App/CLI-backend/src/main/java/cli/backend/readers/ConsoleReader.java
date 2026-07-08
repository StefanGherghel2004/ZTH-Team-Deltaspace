package cli.backend.readers;

import java.util.Scanner;

public class ConsoleReader {

    private final Scanner scanner;

    private enum COMMANDS {
        BACK,
        NEXT,
        OPEN,
        CLOSE
    }

    public ConsoleReader () {

        this.scanner = new Scanner(System.in);
    }

    //Reads a string value and returns it only if it is a integer
    public int readInt () {

        while (true) {

            String inputInteger = scanner.nextLine().trim();

            try{
                return Integer.parseInt(inputInteger);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    //Reads a string value and returns it only if it is a integer and in a specific range
    public int readIntInRange (int min, int max) {

        while (true) {
            System.out.print("Choose an option (" + min + "-" + max + "): ");
            int value = this.readInt();
            if (value >= min && value <= max) {
                return value;
            }
            else
                System.out.println("Invalid option. Please enter a number between" +
                        " " + min + " and " + max + ".");
        }
    }

    public String readString(){
        while (true) {
            String input = scanner.nextLine();
            if (input != null) {
                return input.trim();
            }
            System.out.println("Error: Input cannot be empty. Try again.");
        }
    }
}
