package cli.backend.handlers;

import cli.backend.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserHandler {

    private static UserHandler instance;

    private static final Scanner sc = new Scanner(System.in);

    private static final List<User> users = new ArrayList<>();

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{4,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private UserHandler() {

    }

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }

        return instance;
    }

    public void userRegister() {

        System.out.println("Welcome to the registration page.");

        String name = getValidInput(
                "Please enter your username (4-20 characters, alphanumeric):",
                USERNAME_REGEX,
                "Invalid username format. Please try again."
        );

        String email = getValidInput(
                "Please enter your email address:",
                EMAIL_REGEX,
                "Invalid email format. Must be like 'user@domain.com'."
        );

        String password = getValidInput(
                "Please enter your password (min 8 chars, 1 uppercase, 1 lowercase, 1 number):",
                PASSWORD_REGEX,
                "Invalid password format. Please ensure it meets the requirements."
        );

        users.add(new User(name, email, password));

        System.out.println("Registration successful! Welcome to our platform.");

    }

    public User userLogin() {
        System.out.println("Welcome to the login page.");

        System.out.println("Insert your username:");
        String name = sc.nextLine();

        System.out.println("Insert your password:");
        String password = sc.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(name) && user.getPassword().equals(password)) {
                System.out.println("Successfully logged in into your account - " + user.getUsername() + ".");
                return user;
            }
        }

        System.out.println("Invalid username or password");

        return null;
    }

    // this method will not return without a proper input
    private String getValidInput(String prompt, String regex, String errorMsg) {
        while (true) {
            System.out.println(prompt);
            String input = sc.nextLine();

            if (Pattern.matches(regex, input)) {
                return input;
            }

            System.out.println(errorMsg);
        }
    }

}
