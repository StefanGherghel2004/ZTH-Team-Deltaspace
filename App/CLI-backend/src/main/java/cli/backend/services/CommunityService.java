package cli.backend.services;

import cli.backend.Community;
import cli.backend.repositories.CommunityRepository;
import cli.backend.exceptions.InvalidCommunityException;

import java.util.ArrayList;
import java.util.List;

public class CommunityService {

    private static CommunityService instance;
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

    public List<Community> getCommunities(){
        return communityRepository.getDBCommunities();
    }

    public Community getCommunityByName(String name){
        List<Community> communities = communityRepository.getDBCommunities();

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

        return communityRepository.deleteCommunity(community.getNickname());
    }
    public List<String> getAvailableTopics(){
        return new ArrayList<>(TOPICS);
    }


}
