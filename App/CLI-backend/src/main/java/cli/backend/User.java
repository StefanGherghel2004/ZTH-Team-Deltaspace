package cli.backend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
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

}
