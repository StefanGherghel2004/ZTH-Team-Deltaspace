package cli.backend.services;

import cli.backend.User;
import cli.backend.exceptions.InvalidUserAccount;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{4,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private static final List<User> users = new ArrayList<>();

    public static void addUser (String username, String email, String password, String dateOfBirth){

        users.add(new User(username,email,password,dateOfBirth));
    }

    public static boolean validateUsername (String username) {

        if(validate(username,USERNAME_REGEX) && !username.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean validatePassword (String password) {

        if(validate(password,PASSWORD_REGEX) && !password.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean validateEmail (String email) {
        if (validate(email,EMAIL_REGEX) && !email.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean validateDateOfBirth (String dateOfBirth) {

        if (dateOfBirth.isEmpty())
            return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {

            LocalDate birthday = LocalDate.parse(dateOfBirth, formatter);
            LocalDate today = LocalDate.now();
            Period age = Period.between(birthday, today);
            return age.getYears() >= 13;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format!");
            return false;
        }
    }

    public static User validateUserAccount (String username, String password)
            throws InvalidUserAccount {

        for (User user : users) {
            if(user.getUsername().isEmpty() || user.getPassword().isEmpty())
                throw new InvalidUserAccount("Invalid username or password. Please try again.\n");

            if (user.getUsername().equals(username) && PasswordService.verify(password, user.getPassword()))
                return user;
        }
        throw new InvalidUserAccount("Invalid username or password. Please try again.\n");
    }

    public static boolean validate(String user, String regex) {
        if (Pattern.matches(regex,user))
            return true;
        else
            return false;
    }
}
