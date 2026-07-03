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

        // checking name and password restrictions with
        // additional classes/methods to be in mind
        System.out.println("Insert your username:");

        String name = sc.nextLine();

        System.out.println("Insert your password:");

        String password = sc.nextLine();

        users.add(new User(name, password));

        System.out.println("Registration succesfull! Welcome to out platform.");

    }
}
