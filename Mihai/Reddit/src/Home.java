import java.io.*;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Home {
    public static void main(String[] args) throws IOException, PasswordFormatException, UsernameFormatException {

        enum States {

            LOGIN,
            REGISTER
        }

        Scanner scanner = new Scanner(System.in);
        File userListFile = new File("C:\\Users\\DELL\\Desktop\\UserList.txt");

        System.out.println("Welcome to Reddit! Please choose one of the option: 1. Login 2. Register");

        States state = null;
        while(state == null) {
            String choice = scanner.nextLine();
            if (choice.equals("1")) {

                state = States.LOGIN;
            } else if (choice.equals("2")) {

                state = States.REGISTER;
            } else {

                System.out.println("Invalid option selection.");
            }
        }

        switch (state) {

            case REGISTER:
                System.out.println("Please choose your username.");

                Username username = null;
                Password password = null;
                DateOfBirth dateOfBirth = null;
                while(username == null) {

                    System.out.print("Username:");
                    String inputUsername = scanner.nextLine();
                    try {
                        username = new Username(inputUsername);
                    } catch (UsernameFormatException ignore) {

                    }
                }

                System.out.println("Please enter your password.");
                while(password == null) {

                    System.out.print("Password:");
                    String inputPassword = scanner.nextLine();
                    try {
                        password = new Password(inputPassword);
                    } catch (PasswordFormatException e) {
                        System.out.println(e.toString());
                    }
                }

                System.out.println("Please enter your date of birth.");
                while (dateOfBirth == null) {
                    System.out.print("Date of birth (DD-MM-YY): ");
                    String inputDateOfBirth = scanner.nextLine();
                    try{

                        dateOfBirth = new DateOfBirth(inputDateOfBirth);
                    } catch (AgeRequirementException e) {

                        System.out.println(e.getMessage());
                        return;
                    } catch (DateTimeException e) {

                        System.out.println(e.getMessage());
                    }
                }

                UserAccount userAccount = new UserAccount(username.toString(),password.toString());

                try (BufferedWriter bw = new BufferedWriter((new FileWriter(userListFile,true)))) {
                    bw.write("User ID: " + userAccount.getUserId());
                    bw.newLine();
                    bw.write("Username: " + userAccount.getUsername());
                    bw.newLine();
                    bw.write("Password: " + userAccount.getPassword());
                    bw.newLine();
                    bw.newLine();
                } catch (IOException e){

                    System.out.println("Error writing to the file.");
                }

                System.out.println("Account successfully created!");
                break;

            case LOGIN:

        }
    }
}
