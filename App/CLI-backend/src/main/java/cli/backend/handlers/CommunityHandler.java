package cli.backend.handlers;

import cli.backend.Community;
import cli.backend.Post;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class CommunityHandler {
    public static final List<String> TOPICS = List.of(
            "Food",
            "Gaming",
            "Science",
            "Art",
            "Tech"
    );

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

        showTopicsList();

        int choice;
        String selectedTopic;

        while (true) {
            System.out.print("Choose an option (1-" + TOPICS.size() + "): ");
            try {
                choice = Integer.parseInt(cobj.nextLine().trim());

                if (choice < 1 || choice > TOPICS.size()) {
                    System.out.println("Invalid option. Please enter a valid number.");
                    continue;
                }

                selectedTopic = TOPICS.get(choice - 1);
                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from the list.");
            }
        }

        System.out.println("Please Enter Community Description");
        String description=cobj.nextLine();

        Community community= new Community(selectedTopic, communityName,description);
        communities.add(community);
        System.out.println("Community r/" + community.getNickname() + " successfully created.");
    }

    private void showTopicsList() {
        System.out.println("TOPICS LIST");
        for (int i = 0; i < TOPICS.size(); i++) {
            System.out.println((i + 1) + ". " + TOPICS.get(i));
        }
    }

    public void viewCommunities(){
        if (communities.isEmpty()){
            System.out.println("No communities created");
        }
        else {
            for (Community community : communities) {
                System.out.println(community.getNickname());
            }
        }
    }

    public void viewCommunityPosts(Community community){
        List<Post> communityPosts=community.getPosts();
        if(communityPosts.isEmpty()){
            System.out.print("No posts in this r/");
        }
        else {
            for (Post post : community.getPosts()) {
                System.out.println("ID: " + post.getPostID());
                System.out.println("Title: " + post.getPostTitle());
                System.out.println("Author: " + post.getUser().getUsername());
                System.out.println();
            }
        }
    }
    public List<Community> getCommunities(){
        return communities;
    }


    public Community findCommunityByName(String name) {
        for (Community c : communities) {
            if (c.getNickname().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

}

