package cli.backend.duplicates;

import java.util.List;

public class UserDuplicate implements  CheckDuplicate{
    private static String filePath= "databases/UserDatabase.xlsx";
    private static final int userColumn=1;
    ExcelRead excelRead= ExcelRead.getInstance();
    @Override
    public boolean isDuplicate(String userToCheck) {
        if(userToCheck==null){
            return false;
        }
        List<String> existingUsers=excelRead.getColumnValues(filePath,userColumn);
        for (String u:existingUsers){
            if(u.equalsIgnoreCase(userToCheck)){
                return true;
            }
        }
        return false;
    }
}
