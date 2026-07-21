package cli.backend.duplicates;

import java.util.List;

public class CommunityDuplicate implements  CheckDuplicate{
    private final static String filePath="databases/CommunityDatabase.xlsx";
    private final static int communityColumn=0;
    ExcelRead excelRead = ExcelRead.getInstance();
    @Override
    public boolean isDuplicate(String communityToCheck) {
        List<String> existingCommunities=excelRead.getColumnValues(filePath,communityColumn);
        for(String community:existingCommunities){
            if(community.equalsIgnoreCase(communityToCheck)){
                return true;
            }
        }
        return false;
    }
}
