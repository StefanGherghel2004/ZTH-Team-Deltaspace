package cli.backend;

import cli.backend.database.DatabaseInitialize;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class User {

    private int userID;
    public static int userCounter = 0;

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

    public boolean checkAge(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthday=LocalDate.parse(dateOfBirth, formatter);
        Period age = Period.between(birthday, today);
        return age.getYears() >= 18;
    }

}
