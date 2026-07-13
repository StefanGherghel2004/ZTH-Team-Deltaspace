package cli.backend.services;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.database.ExcelRead;
import cli.backend.database.ExcelWrite;
import cli.backend.exceptions.InvalidCommunityException;

import java.util.ArrayList;
import java.util.List;

public class CommunityService {

    private static CommunityService instance;
    private static ExcelWrite excelWrite = ExcelWrite.getInstance();
    private static ExcelRead excelRead= ExcelRead.getInstance();
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
    private final List<Community> communities=excelRead.getExcelCommunities();


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

    public void addCommunity(String communityCreator, String name, String topic, String description)
            throws InvalidCommunityException {
        if (TOPICS.contains(topic)) {
            communities.add(new Community(communityCreator, topic, name, description));
            excelWrite.write("App/CLI-backend/databases/CommunityDatabase.xlsx",List.of(
                    name,topic,description,communityCreator
            ));
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
        }

        return isRemoved;
    }
    public List<String> getAvailableTopics(){
        return new ArrayList<>(TOPICS);
    }


}
