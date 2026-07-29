package org.example.commands;


import org.example.User;
import org.example.exceptions.DeletedAccountException;
import org.example.exceptions.InvalidUserAccountException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserValidator {

    private static final int MIN_AGE = 13;

    private static UserValidator instance;

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";
    private static final String EMAIL_REGEX = "(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static RestClient restClient = RestClient.create();

    private UserValidator() {}

    public static UserValidator getInstance() {
        if (instance == null) instance = new UserValidator();
        return instance;
    }

    public boolean validateUsername (String username) {
        return (validate(username,USERNAME_REGEX) && !username.isEmpty());
    }

    public boolean validatePassword (String password) {

        return (validate(password,PASSWORD_REGEX) && !password.isEmpty());
    }

    public boolean validateEmail (String email) {
        return (validate(email,EMAIL_REGEX) && !email.isEmpty());
    }

    public boolean validateDateOfBirth (String dateOfBirth) {

        if (dateOfBirth.isEmpty())
            return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {

            LocalDate birthday = LocalDate.parse(dateOfBirth, formatter);
            LocalDate today = LocalDate.now();
            Period age = Period.between(birthday, today);
            return age.getYears() >= MIN_AGE;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format!");
            return false;
        }
    }

    public  boolean validate(String user, String regex) {
        return user != null && Pattern.matches(regex, user);
    }
}
