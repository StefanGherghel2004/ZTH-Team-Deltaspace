package cli.backend.handlers;

import cli.backend.Community;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class CommunityHandler {

    private static CommunityHandler instance;
    private  List<Community> communities;
    private CommunityHandler(){

        this.communities=new java.util.ArrayList<>();

    }
    public static CommunityHandler getInstance(){
        if(instance==null){
            instance=new CommunityHandler();
        }
        return instance;
    }
    public void addCommunity(){
        Scanner cobj= new Scanner(System.in);

        System.out.println("Please Enter Community Name:");
        String communityName=cobj.nextLine();

        System.out.println("Please Enter Community Topic:");
        String topic=cobj.nextLine();

        System.out.println("Please Enter Community Description");
        String description=cobj.nextLine();

        Community community= new Community(topic,communityName,description);
        communities.add(community);
        System.out.println("Community r/" + community.getNickname() + "successfully created.");
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



