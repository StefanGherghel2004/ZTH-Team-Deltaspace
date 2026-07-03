package cli.backend.handlers;

import cli.backend.Community;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommunityHandler {
    private List<Community> communities=new ArrayList<>();
    private static CommunityHandler instance;
    public static CommunityHandler getInstance(){
        if(instance==null){
            instance=new CommunityHandler();
        }
        return instance;
    }
    public void addComunity(){
        Scanner cobj= new Scanner(System.in);

        System.out.println("Please Enter Community Name:");
        String communityName=cobj.nextLine();

        System.out.println("Please Enter Community Topic:");
        String topic=cobj.nextLine();

        System.out.println("Please Enter Community Description");
        String description=cobj.nextLine();

        Community community= new Community(topic,communityName,description);
        communities.add(community);
    }
    public void viewCommunities(){
        if (communities.isEmpty()){
            System.out.println("No communities created");
        }
        else {
            for(Community community:communities){
                System.out.println("r/" + community.getNickname() + " - " + community.getDescription());
            }
        }
    }
    }



