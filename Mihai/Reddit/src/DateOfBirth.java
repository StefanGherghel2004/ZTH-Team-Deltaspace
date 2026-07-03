import java.time.Year;
import java.time.LocalDate;
import java.time.Period;

public class DateOfBirth {

    private String dateOfBirth;
    private String dateError;

    public DateOfBirth (String dateOfBirth) {

        String[] characterSplitDateOfBirth = dateOfBirth.split("-");

        int day = Integer.parseInt(characterSplitDateOfBirth[0]);
        int month = Integer.parseInt(characterSplitDateOfBirth[1]);
        int year = Integer.parseInt(characterSplitDateOfBirth[2]);

        //Calculating the age of the user using the current date
        LocalDate birthday = LocalDate.of(year, month, day);
        LocalDate today = LocalDate.now();
        Period agePeriod = Period.between(birthday,today);
        int age = agePeriod.getYears();

        if (day > 31 || month > 12 || (year > Year.now().getValue())) {

            System.out.println("Please enter a valid date.");
            this.dateError = "InvalidDateError";
        }
        else if (age < 13) {

            System.out.println("You need to be at least 13 years of age to create an account.");
            this.dateError = "NotOldEnoughError";
        }
        else {

            this.dateOfBirth = dateOfBirth;
            this.dateError = "NoError";
        }
    }
}
