package cli.backend.duplicates;

import cli.backend.Community;
import cli.backend.database.CommentRepository;
import cli.backend.database.CommunityRepository;

import java.util.List;

public class CommunityDuplicate implements  CheckDuplicate{
    private final static int communityColumn=0;
    private final static CommunityRepository communityRepository = CommunityRepository.getInstance();
    @Override
    public boolean isDuplicate(String communityToCheck) {
        List<Community> existingCommunities=communityRepository.getDBCommunities();
        for(Community community:existingCommunities){
            if(community.getNickname().equalsIgnoreCase(communityToCheck)){
                return true;
            }
        }
        return false;
    }
}
