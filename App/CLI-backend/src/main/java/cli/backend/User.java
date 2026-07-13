package cli.backend;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

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
        getCurrentUserId();
        userID = ++userCounter;
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

    public void getCurrentUserId() {
        File file = new File("App/CLI-backend/databases/UserDatabase.xlsx");

        if (!file.exists() || file.length() == 0) {
            userCounter = 0;
            return;
        }

        try (FileInputStream in = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            if (workbook.getNumberOfSheets() == 0) {
                userCounter = 0;
                return;
            }
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastRowIndex = sheet.getLastRowNum();

            if (lastRowIndex == 0) {
                userCounter = 0;
                return;
            }

            Row row = sheet.getRow(lastRowIndex);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = cell.toString().trim();
                    if (cellValue.endsWith(".0")) {
                        cellValue = cellValue.substring(0, cellValue.length() - 2);
                    }
                    userCounter = Integer.parseInt(cellValue);
                    return;
                }
            }
            userCounter = 0;
        } catch (IOException | NumberFormatException e) {
            userCounter = 0;
        }
    }
}
