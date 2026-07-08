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
        this.dateOfBirth = dateOfBirth;
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

}
