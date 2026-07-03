package cli.backend.handlers;

import cli.backend.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserHandler {

    private static UserHandler instance;

    private static final Scanner sc = new Scanner(System.in);

    private static final List<User> users = new ArrayList<>();

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

        // checking name and password restrictions with
        // additional classes/methods to be in mind
        System.out.println("Please enter your username:");

        String name = sc.nextLine();

        System.out.println("Please enter your email address:");

        String email = sc.nextLine();

        System.out.println("Please enter your password:");

        String password = sc.nextLine();

        users.add(new User(name, email, password));

        System.out.println("Registration successful! Welcome to out platform.");

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
}
