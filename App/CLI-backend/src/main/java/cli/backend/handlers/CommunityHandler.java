package cli.backend.handlers;

import cli.backend.Community;

import java.util.Scanner;

public class CommunityHandler {
    private String communityName;
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

        communityName=cobj.nextLine();


        System.out.println("Please Enter Community Topic:");
        String topic=cobj.nextLine();

        System.out.println("Please Enter Community Description");
        String description=cobj.nextLine();

        Community community= new Community(topic,communityName,description);
    }
}
