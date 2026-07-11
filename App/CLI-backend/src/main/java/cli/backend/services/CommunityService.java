package cli.backend.services;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.User;
import cli.backend.exceptions.InvalidCommunityException;

import java.util.ArrayList;
import java.util.List;

public class CommunityService {

    private static CommunityService instance;

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
    private final List<Community> communities=new ArrayList<>();


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

    public void addCommunity(User communityCreator, String name, String topic, String description)
            throws InvalidCommunityException {
        if (TOPICS.contains(topic)) {
            communities.add(new Community(communityCreator, topic, name, description));
        }
        else {
            throw new InvalidCommunityException("Invalid topic. Please choose from the available topics.");
        }
    }
    public List<String> getAvailableTopics(){
        return new ArrayList<>(TOPICS);
    }


}
