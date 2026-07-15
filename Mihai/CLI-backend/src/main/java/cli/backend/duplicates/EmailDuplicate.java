package cli.backend.duplicates;

import cli.backend.database.ExcelRead;

import java.util.List;

public class EmailDuplicate implements CheckDuplicate {
    private static final String filePath="App/CLI-backend/databases/UserDatabase.xlsx";
    private static final int emailColumn=2;
    ExcelRead excelRead = ExcelRead.getInstance();
    @Override
    public boolean isDuplicate(String emailToCheck) {
        List<String> existingEmails=excelRead.getColumnValues(filePath,emailColumn);
        for(String email:existingEmails){
            if(email.equalsIgnoreCase(emailToCheck)){
                return true;
            }
        }
        return false;
    }
}
