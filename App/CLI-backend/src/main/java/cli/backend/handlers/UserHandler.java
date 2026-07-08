package cli.backend.handlers;

import cli.backend.User;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserHandler {

    private static UserHandler instance;

    private static final Scanner sc = new Scanner(System.in);

    public static final List<User> users = new ArrayList<>();

    private UserHandler() {
        users.add(new User(
                "admin",
                "admin@test",
                PasswordService.hash("Admin123"),
                "01-01-2000"
        ));
    }

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();

        }
        return instance;
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
