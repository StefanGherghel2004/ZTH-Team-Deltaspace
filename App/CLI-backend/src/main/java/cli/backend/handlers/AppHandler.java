package cli.backend.handlers;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;

import java.util.Scanner;

public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN,
        SHOW_COMMUNITIES,
        IN_COMMUNITY,
        SHOW_POSTS,
        ON_POST
    }

    private State currentState = State.NOT_LOGGED_IN;
    private User currentUser;
    private Community currentCommunity;
    private Post currentPost;

    private static AppHandler instance;

    private static Scanner sc = new Scanner(System.in);

    private static UserHandler userHandler = UserHandler.getInstance();
    private static PostHandler postHandler = PostHandler.getInstance();
    private static CommunityHandler communityHandler=CommunityHandler.getInstance();
    private static CommentHandler commentHandler= CommentHandler.getInstance();

    private AppHandler() {

    }

    public static AppHandler getInstance() {

        if (instance == null) {
            instance = new AppHandler();

        }

        return instance;

    }

    public void run() {
        // some function to handle the UI maybe moved later in another class
        boolean isActive = true;

        while(isActive) {
            isActive = handleState();
        }
    }

    private boolean handleState() {

        // this switch handles the first prompt of that page
        switch(currentState) {
            case NOT_LOGGED_IN:
                System.out.println("\nWelcome to Deltaspace platform");
                System.out.println("1. Register\n2. Login\n3. Quit");
                break;
            case LOGGED_IN:
                System.out.println("\n1. Show feed\n2. Create community\n3. Create Post\n4. Show communities\n5. Logout");
                break;
            case SHOW_COMMUNITIES:
                System.out.println("Choose a community");
                communityHandler.viewCommunities();
                break;
            case IN_COMMUNITY:
                System.out.println("\n1.View Posts\n2.Add Post\n3. Return to Main Menu");
                break;
            case SHOW_POSTS:
                communityHandler.viewCommunityPosts(currentCommunity);
                break;
            case ON_POST:
                postHandler.viewPost(currentPost);
                System.out.println("\n1. Show comments\n2. Add comment\n3. Reply to comment \n4. Back");
                break;

        }


        if(currentState==State.SHOW_COMMUNITIES){

            if (communityHandler.getCommunities().isEmpty()) {
                currentState = State.LOGGED_IN;
            } else {
                String communityName = sc.nextLine().trim();

                Community foundCommunity = communityHandler.findCommunityByName(communityName);

                if (foundCommunity != null) {
                    currentCommunity = foundCommunity;
                    currentState = State.IN_COMMUNITY;
                } else {
                    System.out.println("Community not found!");
                    currentState = State.LOGGED_IN;
                }
            }
            return true;
        }

        if(currentState == State.SHOW_POSTS){

            if(currentCommunity.getPosts().isEmpty()){
                currentState = State.IN_COMMUNITY;
                return true;
            }

            System.out.println("Choose a post [ID]:");
            int id = Integer.parseInt(sc.nextLine());

            currentPost = currentCommunity.findPostById(id);

            if(currentPost != null){
                currentState = State.ON_POST;
            } else {
                System.out.println("Post not found!");
            }

            return true;
        }

        int command;

        while (true) {
            System.out.print("Choose an option: ");
            try {
                command = Integer.parseInt(sc.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from the list.");
            }
        }
        if (currentState == State.NOT_LOGGED_IN) {
            switch(command) {
                case 1:
                    userHandler.userRegister();
                    break;
                case 2:
                    User loggedInUser = userHandler.userLogin();
                    if (loggedInUser != null) {
                        currentUser = loggedInUser;
                        currentState = State.LOGGED_IN;
                    }
                    break;
                case 3:
                    System.out.println("Shutting down...");
                    return false;
            }
        }
        else if (currentState == State.LOGGED_IN) {
            switch(command) {
                case 1:
                    postHandler.viewFeed();
                    break;
                case 2:
                    communityHandler.addCommunity();
                    break;
                case 3:
                    postHandler.addPost(currentUser);
                    break;
                case 4:
                    currentState=State.SHOW_COMMUNITIES;
                    break;
                case 5:
                    System.out.println("Logging out...");
                    currentUser = null;
                    currentState = State.NOT_LOGGED_IN;
                    break;
            }
        }

        else if(currentState==State.IN_COMMUNITY){
            switch(command){
                case 1:
                    currentState = State.SHOW_POSTS;
                    break;
                case 2:
                    Post post = postHandler.addPost(currentUser, currentCommunity);
                    currentCommunity.getPosts().add(post);
                    break;
                case 3:
                    System.out.println("Returning to Main Menu");
                    currentCommunity=null;
                    currentState=State.LOGGED_IN;
                    break;


            }
        } else if(currentState == State.ON_POST){

            switch(command){

                case 1:
                    postHandler.showComments(currentPost);
                    break;

                case 2:
                    commentHandler.addComment(currentUser, currentPost);
                    break;

                case 3:
                    commentHandler.replyToComment(currentUser, currentPost);
                    break;
                case 4:
                    currentPost = null;
                    currentState = State.IN_COMMUNITY;
                    break;
            }
        }

        return true;

    }
}
