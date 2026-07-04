package cli.backend;

import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class User {

    private int userID;
    static int userCounter = 0;

    private String name;
    private String email;
    private String password;
    private String dateOfBirth;

    public User(String name, String email, String password, String dateOfBirth) {
        this.name = name;
        this.email = email;
        this.password = password;
        userID = userCounter++;
    }

    public String getUsername() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDateOfBirth() { return dateOfBirth;}

    public int getUserID() {
        return userID;
    }

    public static boolean checkUserDateOfBirth (String dateOfBirth) {

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
}
