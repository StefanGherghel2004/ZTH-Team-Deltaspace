package cli.backend.database;

import cli.backend.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ExcelRead {

    private static ExcelRead instance;
    private ExcelRead() {
    }

    public static ExcelRead getInstance()
    {
        if(instance==null){
            instance=new ExcelRead();

        }
        return instance;
    }

    public void readExcel(String filename)  {
        System.out.println("Attempting to open Excel");
        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i=0;i<=sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                for(int j=0;j<row.getLastCellNum();j++){
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell);
                    System.out.printf("%-16s", cellValue);
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();

        }
    }
    public boolean checkDuplicateUsername(String usernameToCheck,String filename){

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for(int i=1;i<=sheet.getLastRowNum();i++){
                Row row=sheet.getRow(i);
                if(row==null){
                    continue;
                }
                Cell usernameCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String username = formatter.formatCellValue(usernameCell).trim();
                if(username.isEmpty()){
                    continue;
                }
                if(username.equalsIgnoreCase(usernameToCheck.trim())){
                    return true;
                }

            }

        }catch (IOException e){
            System.out.println("File not found");
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicateEmail(String emailToCheck,String filename){

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                DataFormatter formatter = new DataFormatter();
                for(int i=1;i<=sheet.getLastRowNum();i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    Cell emailCell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String email = formatter.formatCellValue(emailCell).trim();
                    if (email.isEmpty()) {
                        continue;
                    }
                    if (email.equalsIgnoreCase(emailToCheck.trim())){
                       return true;
                    }

                }
            }catch(IOException e){
                System.out.println("File not found");
                e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicateCommmunity(String communityNameToCheck , String filename) {
        try(FileInputStream file = new FileInputStream(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for(int i=1;i<=sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                Cell communityCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String communityName = formatter.formatCellValue(communityCell).trim();
                if(communityName.isEmpty()){
                    continue;
                }
                if(communityName.equalsIgnoreCase(communityNameToCheck.trim())){
                    return true;
                }
            }
    }catch(IOException e){
            System.out.println("File not found");
            e.printStackTrace();
        }
        return false;
    }
    public List<User> getExcelUsers(){

        String filename = "App/CLI-backend/databases/UserDatabase.xlsx";
        List<User> excelUsers = new ArrayList<>();

        try(FileInputStream file = new FileInputStream(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for(int i=1;i<=sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }

                String userNameCell = formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String emailCell = formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String passwordCell = formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String dateOfBirthCell = formatter.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                if(userNameCell.isEmpty()) {
                    continue;
                }
                excelUsers.add(new User(userNameCell,emailCell,passwordCell,dateOfBirthCell));

            }
        }catch(IOException e){
            System.out.println("File not found");
            e.printStackTrace();
        }

    return excelUsers;
    }
}

