package cli.backend.database;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelRead {

    private static ExcelRead instance;

    private ExcelRead() {
    }

    public static ExcelRead getInstance() {
        if (instance == null) {
            instance = new ExcelRead();
        }
        return instance;
    }

    public void readExcel(String filename) {
        System.out.println("Attempting to open Excel");
        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                for (int j = 0; j < row.getLastCellNum(); j++) {
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

    public List<String> getColumnValues(String filename, int columnIndex) {
        List<String> columnValues = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = formatter.formatCellValue(cell).trim();
                if (!cellValue.isEmpty()) {
                    columnValues.add(cellValue);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        return columnValues;
    }

    public List<User> getExcelUsers() {
        String filename = "App/CLI-backend/databases/UserDatabase.xlsx";
        List<User> excelUsers = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String userNameCell = formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String emailCell = formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String passwordCell = formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String dateOfBirthCell = formatter.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                if (userNameCell.isEmpty()) {
                    continue;
                }
                excelUsers.add(new User(userNameCell, emailCell, passwordCell, dateOfBirthCell));
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }

        return excelUsers;
    }

    public List<Community> getExcelCommunities() {
        String filename = "App/CLI-backend/databases/CommunityDatabase.xlsx";
        List<Community> excelCommunities = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String communityNamecell = formatter.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String communityTopicCell = formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String communityDescriptionCell = formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String communityUserCell = formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                if (communityNamecell.isEmpty()) {
                    continue;
                }
                // Order matched constructor signature: Creator/User, Topic, Name (Nickname), Description
                Community community = new Community(communityUserCell, communityTopicCell, communityNamecell, communityDescriptionCell);
                excelCommunities.add(community);
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }

        return excelCommunities;
    }

    // FIXED: Reads posts, maps users and updates communities IN-MEMORY efficiently
    public List<Post> getExcelPosts() {
        String filename = "App/CLI-backend/databases/PostDatabase.xlsx";
        List<Post> excelPosts = new ArrayList<>();

        // Cache these collections outside of the loop so we don't hit the disk constantly!
        List<User> users = getExcelUsers();
        List<Community> communities = getExcelCommunities();

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String postIDCell = formatter.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postUserCell = formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postTitleCell = formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postContentCell = formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postImageLinkCell = formatter.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postCommunityNameCell = formatter.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postNSFWCell = formatter.formatCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));

                if (postUserCell.isEmpty()) {
                    continue;
                }
                boolean isNSFW = Boolean.parseBoolean(postNSFWCell);

                // Fast User Matching
                User user = null;
                for (User u : users) {
                    if (u.getUsername().equalsIgnoreCase(postUserCell)) {
                        user = u;
                        break;
                    }
                }
                if (postCommunityNameCell.equalsIgnoreCase("None")) {
                    postCommunityNameCell = "u/" + postUserCell;
                }

                Post post = new Post(user.getUsername(), postTitleCell, postContentCell, postImageLinkCell, isNSFW, postCommunityNameCell);
                post.setId(Long.parseLong(postIDCell));
                excelPosts.add(post);

                // Allocate post to the cached community instance
                for (Community c : communities) {
                    if (c.getNickname().equalsIgnoreCase(postCommunityNameCell)) {
                        c.addPost(post);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        return excelPosts;
    }

    // FIXED: Reads all posts and returns the list of communities with posts successfully attached
    public List<Community> getCommunityExcelPosts() {
        String filename = "App/CLI-backend/databases/PostDatabase.xlsx";
        List<User> excelUsers = getExcelUsers();
        List<Community> communities = getExcelCommunities();

        try (FileInputStream file = new FileInputStream(filename);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String postIDCell = formatter.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postUserCell = formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postTitleCell = formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postContentCell = formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postImageLinkCell = formatter.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postCommunityNameCell = formatter.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                String postNSFWCell = formatter.formatCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));

                if (postUserCell.isEmpty()) {
                    continue;
                }

                boolean isNSFW = Boolean.parseBoolean(postNSFWCell);

                // Fast User Matching
                User user = null;
                for (User u : excelUsers) {
                    if (u.getUsername().equalsIgnoreCase(postUserCell)) {
                        user = u;
                        break;
                    }
                }

                if (postCommunityNameCell.equalsIgnoreCase("None")) {
                    postCommunityNameCell = "u/" + postUserCell;
                }

                Post post = new Post(user.getUsername(), postTitleCell, postContentCell, postImageLinkCell, isNSFW, postCommunityNameCell);
                post.setId(Long.parseLong(postIDCell));

                // Allocate post to the target community
                for (Community c : communities) {
                    if (c.getNickname().equalsIgnoreCase(postCommunityNameCell)) {
                        c.addPost(post);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        return communities;
    }
}