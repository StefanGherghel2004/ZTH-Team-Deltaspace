package cli.backend.services;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.database.CommunityRepository;
import cli.backend.exceptions.InvalidCommunityException;

import java.util.ArrayList;
import java.util.List;

public class CommunityService {

    private static CommunityService instance;
    private static ExcelWrite excelWrite = ExcelWrite.getInstance();
    private static ExcelRead excelRead= ExcelRead.getInstance();
    private  static ExcelDelete excelDelete= ExcelDelete.getInstance();
    private static CommunityRepository communityRepository = CommunityRepository.getInstance();
    public static CommunityService getInstance(){
        if(instance==null){
            instance = new CommunityService();
        }
        return instance;
    }

    public static final List<String> TOPICS=List.of(
            "Food",
            "Gaming",
            "Science",
            "Art",
            "Tech"
    );
    private final List<Community> communities= communityRepository.getDBCommunities();


    public List<Community> getCommunities(){
        return communities;
    }

    public Community getCommunityByName(String name){
        for (Community c:communities){
            if(c.getNickname().equalsIgnoreCase(name)){
                return c;
            }
        }
        return null;
    }

    public String formatName(String name) {
        if (!name.startsWith("r/")) {
            name = "r/" + name;
        }
        return name;
    }

    public void addCommunity(Long communityCreator, String name, String topic, String description)
            throws InvalidCommunityException {
        if (TOPICS.contains(topic)) {
            Community community = new Community(communityCreator, topic, name, description);
            communities.add(community);
            communityRepository.addCommunity(community);
        }
        else {
            throw new InvalidCommunityException("Invalid topic. Please choose from the available topics.");
        }
    }

    public boolean deleteCommunity(Community community) {
        if (community == null) {
            return false;
        }

        boolean isRemoved = communities.remove(community);

        if (isRemoved) {
            List<Post> postsToDelete = community.getPosts();
            for (Post p : postsToDelete) {
                PostService.getInstance().deletePost(p);
            }
            boolean dataBaseDeleted = communityRepository.deleteCommunity(community.getNickname());
        }

        return isRemoved;
    }
    public List<String> getAvailableTopics(){
        return new ArrayList<>(TOPICS);
    }


}
