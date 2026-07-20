package cli.backend;

import cli.backend.database.ExcelWrite;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private boolean deleted = false;

    public User(String username, String email, String password, LocalDate dateOfBirth) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public boolean checkAge(){
        LocalDate today = LocalDate.now();
        Period age = Period.between(dateOfBirth, today);
        return age.getYears() >= 18;
    }

    public void setId(long id) {
        this.id = id;
    }
}
