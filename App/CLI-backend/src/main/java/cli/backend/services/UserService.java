package cli.backend.services;

import cli.backend.User;
import cli.backend.database.UserRepository;
import cli.backend.exceptions.InvalidUserAccountException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final int MIN_AGE = 13;

    private final UserRepository userRepository;

    private static UserService instance;

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{4,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private UserService() {
        this.userRepository = UserRepository.getInstance();
    }

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public void addUser(String username, String email, String password, String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob = LocalDate.parse(dateOfBirth, formatter);

        User user = new User(username, email, password, dob);
        userRepository.addUser(user);
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

    public  User validateUserAccount (String usernameOrEmail, String password)
            throws InvalidUserAccountException {

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (PasswordService.verify(password, user.getPassword())) {
                return user;
            }
        }

        throw new InvalidUserAccountException("Invalid username/email or password. Please try again.\n");
    }

    public  boolean validate(String user, String regex) {
        return user != null && Pattern.matches(regex, user);
    }


    public boolean deleteUser(User user) {
        userRepository.deleteUserById(user.getId());
        return true;
    }
}
