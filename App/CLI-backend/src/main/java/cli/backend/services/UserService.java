package cli.backend.services;

import cli.backend.User;
import cli.backend.database.ExcelRead;
import cli.backend.database.ExcelWrite;
import cli.backend.exceptions.InvalidUserAccountException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private static final int MIN_AGE = 13;

    private static UserService instance;

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{4,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private final List<User> users = new ArrayList<>();
    private static ExcelWrite excelWrite = ExcelWrite.getInstance();
    private static ExcelRead excelRead = ExcelRead.getInstance();
    private UserService() {

        this.addUser("admin",
                "test@admin",
                PasswordService.hash("Admin123"),
                "01-01-2000");

    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void addUser (String username, String email, String password, String dateOfBirth){

        User user = new User(username,email,password,dateOfBirth);
        excelWrite.write("App/CLI-backend/databases/UserDatabase.xlsx", List.of(
                String.valueOf(user.getUserID()),user.getUsername(),user.getEmail(),user.getPassword(),
                String.valueOf(user.getDateOfBirth())));
        users.add(user);
    }

    public boolean validateUsername (String username) {

        if(validate(username,USERNAME_REGEX) && !username.isEmpty()){
            return !excelRead.checkDuplicateUsername(username,"App/CLI-backend/databases/UserDatabase.xlsx");
        }
        return false;
    }

    public boolean validatePassword (String password) {

        return (validate(password,PASSWORD_REGEX) && !password.isEmpty());
    }

    public boolean validateEmail (String email) {
        if(validate(email,EMAIL_REGEX) && !email.isEmpty()){
        return !excelRead.checkDuplicateEmail(email,"App/CLI-backend/databases/UserDatabase.xlsx");
        }
        return false;
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

    public  User validateUserAccount (String usernameOrEmail, String password)
            throws InvalidUserAccountException {

        for (User user : users) {

            if ((user.getUsername().equals(usernameOrEmail) || user.getEmail().equals(usernameOrEmail))
            && PasswordService.verify(password, user.getPassword()))
                return user;
        }

        throw new InvalidUserAccountException("Invalid username/email or password. Please try again.\n");
    }

    public  boolean validate(String user, String regex) {
        return user != null && Pattern.matches(regex, user);
    }


}
