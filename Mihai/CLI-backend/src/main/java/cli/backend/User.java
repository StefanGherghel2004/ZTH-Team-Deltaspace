package cli.backend;

import cli.backend.database.ExcelWrite;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
public class User {

    private int userID;
    public static int userCounter = 0;

    private String username;
    private String email;
    private String password;
    private String dateOfBirth;

    public User(String username, String email, String password, String dateOfBirth) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        userCounter = ExcelWrite.getCurrentId(ExcelWrite.getInstance().userDatabasePath);
        userID = ++userCounter;
    }

    public boolean checkAge(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthday=LocalDate.parse(dateOfBirth, formatter);
        Period age = Period.between(birthday, today);
        return age.getYears() >= 18;
    }
}
