package cli.backend.handlers;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.exceptions.InvalidCommunityException;
import cli.backend.exceptions.InvalidUserAccountException;
import cli.backend.services.*;
import java.util.*;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommentService;
import cli.backend.services.CommunityService;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;
import cli.backend.userinterface.MainMenu;
import cli.backend.userinterface.Menu;
import cli.backend.userinterface.StartMenu;

import java.util.List;

public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN,
        SHOW_FEED,
        SHOW_COMMUNITIES,
        IN_COMMUNITY,
        SHOW_POSTS_COMMUNITY,
        ON_POST,
        ON_COMMENT
    }

    private State currentState = State.NOT_LOGGED_IN;

    private User currentUser;
    private Community currentCommunity;
    private Post currentPost;
    private Comment currentComment;

    private static AppHandler instance;

    private static ConsoleReader consoleReader = new ConsoleReader();

    private static final PostService postService=PostService.getInstance();
    private static final UserService userService = UserService.getInstance();
    private static final CommentService commentService = CommentService.getInstance();
    private static final CommunityService communityService = CommunityService.getInstance();
    private static Menu menus;

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
        return switch (currentState) {
            case NOT_LOGGED_IN -> handleNotLoggedIn();
            case LOGGED_IN -> handleLoggedIn();
            case SHOW_FEED -> handleShowFeed();
            case SHOW_COMMUNITIES -> handleShowCommunities();
            case IN_COMMUNITY -> handleInCommunity();
            case SHOW_POSTS_COMMUNITY -> handleShowPostsInCommunity();
            case ON_POST -> handleOnPost();
            case ON_COMMENT -> handleOnComment();
            default -> false;
        };
    }

    private boolean handleNotLoggedIn() {

        menus = new StartMenu();
        menus.showMenu();

        int command = consoleReader.readIntInRange(1,3);

        switch(command) {
            case 1:
                userRegistration();
                break;
            case 2:
                userLogin();
                break;
            case 3:
                System.out.println("Shutting down...");
                return false;
        }
        return true;
    }

    private boolean handleLoggedIn() {

        menus = new MainMenu();
        menus.showMenu();

        int command = consoleReader.readIntInRange(1,5);

        switch(command) {
            case 1:
                currentState = State.SHOW_FEED;
                break;
            case 2:
                createCommunity();
                break;
            case 3:
                createPost();
                break;
            case 4:
                currentState = State.SHOW_COMMUNITIES;
                break;
            case 5:
                System.out.println("Logging out...");
                currentUser = null;
                currentState = State.NOT_LOGGED_IN;
                break;
        }
        return true;
    }

    private boolean handleCreateCommunity()  {
        System.out.println("\n--- Create Community ---");
        System.out.print("Please enter community name: \nr/");
        String communityName = consoleReader.readString();
        List<String> topics = communityService.getAvailableTopics();

        int choice;
        String selectedTopic;
        System.out.println("TOPICS LIST");
        for(String i:topics){
            System.out.println((topics.indexOf(i) + 1) + ". " + i);
        }

        choice = consoleReader.readIntInRange(1,topics.size());
        selectedTopic = topics.get(choice - 1);

        System.out.println("Please Enter Community Description");
        String description = consoleReader.readString();


        try {
            communityService.addCommunity(communityName, selectedTopic, description);
            System.out.println("Community " + communityName + " successfully created.");
            currentState = State.LOGGED_IN;
        } catch (InvalidCommunityException e) {
            System.out.println(e.getMessage());
        }
        return true;

    }

    private boolean handleShowCommunities() {
        System.out.println("\n--- Communities ---");
        List<Community> communities = communityService.getCommunities();
        if (communities.isEmpty()) {
            System.out.println("No communities created");
            currentState = State.LOGGED_IN;
            return true;
        } else {
            for (Community c : communities) {
                System.out.println(c.getNickname());
            }
        }

        System.out.print("\nChoose a community (or press Enter to go back): ");
        String communityName = consoleReader.readString();

        if (communityName.isEmpty()) {
            currentState = State.LOGGED_IN;
            return true;
        }

        Community foundCommunity = communityService.getCommunityByName(communityName);
        if (foundCommunity != null) {
            currentCommunity = foundCommunity;
            currentState = State.IN_COMMUNITY;
        } else {
            System.out.println("Community not found!");
            currentState = State.LOGGED_IN;
        }
        return true;
    }

    private boolean handleInCommunity() {
        System.out.println("\n--- " + currentCommunity.getNickname() + " ---");
        System.out.println("1. View Posts");
        System.out.println("2. Add Post");
        System.out.println("3. Return to Main Menu");

        int command = consoleReader.readIntInRange(1, 3);

        switch(command){
            case 1:
                currentState = State.SHOW_POSTS_COMMUNITY;
                break;
            case 2:
                createPost();
                break;
            case 3:
                System.out.println("Returning to Main Menu...");
                currentCommunity = null;
                currentState = State.LOGGED_IN;
                break;
        }
        return true;
    }

    private boolean handleShowPostsInCommunity() {
        System.out.println("\n--- Posts in " + currentCommunity.getNickname() + " ---");
        List<Post> communityPosts=currentCommunity.getPosts();
        if(communityPosts.isEmpty()){
            System.out.print("No posts in this r/");
        }
        else {
            for (Post post : communityPosts) {
                System.out.println("ID: " + post.getPostID());
                System.out.println("Title: " + post.getPostTitle());
                System.out.println("Author: " + post.getUser().getUsername());
                System.out.println();
            }
        }

        if(currentCommunity.getPosts().isEmpty()){
            currentState = State.IN_COMMUNITY;
            return true;
        }

        System.out.print("Choose a post [ID] (or press Enter to go back): ");
        String input = consoleReader.readString();



        if (input.isEmpty()) {
            currentState = State.IN_COMMUNITY;
            return true;
        }

        try {
            int id = Integer.parseInt(input);
            currentPost = currentCommunity.findPostById(id);

            if(currentPost != null){
                if(currentPost.getNSFW() && !currentUser.checkAge()) {
                    System.out.println("This post is marked as NSFW. You must be at least 18 years old to view it.");
                    currentPost = null;
                    currentState=State.IN_COMMUNITY;
                }
                else {
                    currentState = State.ON_POST;
                }
            } else {
                System.out.println("Post not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }

        return true;
    }

    private boolean handleOnPost() {
        System.out.println("\n--- Viewing Post ---");
        System.out.println("ID: " + currentPost.getPostID());
        System.out.println("Community: " + currentPost.getCommunityName());
        System.out.println("Author: " + currentPost.getUser().getUsername());
        System.out.println("Title: " + currentPost.getPostTitle());
        System.out.println("NSFW: " + (currentPost.getNSFW()? "Yes":"No"));
        if (currentPost.getImageLink() != null) {

            System.out.println("Image: " + currentPost.getImageLink());
        }
        System.out.println("Content: " + currentPost.getPostContents());
        System.out.println("Comments counter: " + currentPost.getCommentsCount() + "\n");

        System.out.println("1. Show comments");
        System.out.println("2. Add comment");
        System.out.println("3. Select comment (Reply)");
        if(currentPost.getCommunityName().equalsIgnoreCase("u/" + currentPost.getUser().getUsername())) {
            System.out.println("4. Back to Main Menu");
        }
        else {
            System.out.println("4. Back to Community");
        }

        int command = consoleReader.readIntInRange(1, 4);

        switch(command){
            case 1:
                showCommentsThread();
                break;
            case 2:
                writeCommentToPost();
                break;
            case 3:
                selectCommentToReply();
                break;
            case 4:
                currentPost = null;
                if(currentCommunity!=null) {
                    currentState = State.IN_COMMUNITY;
                }
                else {
                    currentState=State.LOGGED_IN;
                }
                break;
        }
        return true;
    }

    private boolean handleOnComment() {
        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());

        System.out.println("\n1. Reply");
        System.out.println("2. Back to Post");

        int command = consoleReader.readIntInRange(1, 2);

        switch(command) {
            case 1:
                System.out.print("Write reply: ");
                String text = consoleReader.readString();

                try {
                    commentService.replyToComment(currentUser, currentPost, currentComment, text);
                    System.out.println("Reply added successfully!");
                } catch (EmptyCommentException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                currentComment = null;
                currentState = State.ON_POST;
                break;
        }
        return true;
    }

    private boolean handleShowFeed() {
        System.out.println("\n--- Your Feed ---");
        List<Post> posts=postService.getPosts();
        for(Post post: posts) {
            System.out.println("ID: " + post.getPostID());
            System.out.println("Community: " + post.getCommunityName());
            System.out.println("Author: " + post.getUser().getUsername());
            System.out.println("Title: " + post.getPostTitle());
            if (post.getImageLink() != null) {

                System.out.println("Image: " + post.getImageLink());
            }
            System.out.println("Content: " + post.getPostContents());
            System.out.println("Comments counter: " + post.getCommentsCount() + "\n");
        }

        if(postService.getPosts().isEmpty()){
            System.out.println(postService.getPosts());
            System.out.print("\nPress Enter to return to Main Menu...");
            consoleReader.readString();
            currentState = State.LOGGED_IN;
            return true;
        }

        System.out.print("Choose a post [ID] (or press Enter to go back): ");
        String input = consoleReader.readString();

        if (input.isEmpty()) {
            currentState = State.LOGGED_IN;
            return true;
        }

        try {
            int id = Integer.parseInt(input);

            Post foundPost = postService.findPostById(id);

            if(foundPost != null){
                currentPost = foundPost;
                if(currentPost.getNSFW() && !currentUser.checkAge()) {
                    System.out.println("This post is marked as NSFW. You must be at least 18 years old to view it.");
                    currentPost = null;
                    currentState=State.SHOW_FEED;
                }
                else {
                    currentState = State.ON_POST;
                }
            } else {
                System.out.println("Post not found in feed!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }

        return true;
    }

    private void userLogin() {
        System.out.println("Welcome to the login page.");
        while (true) {
            System.out.println("Insert your username or email:");
            String loginUsername = consoleReader.readString();

            System.out.println("Insert your password:");
            String loginPassword = consoleReader.readString();

            try {
                currentUser = userService.validateUserAccount(loginUsername, loginPassword);
                System.out.println("Successfully logged in into your account - " + currentUser.getUsername());
                currentState = State.LOGGED_IN;
                break;
            } catch (InvalidUserAccountException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void userRegistration() {
        System.out.println("Welcome to the registration page.");

        System.out.println("Please enter your username (4-20 characters, alphanumeric):");
        String username;
        while (!userService.validateUsername(username = consoleReader.readString())){
            System.out.println("Invalid username format. Please try again.");
        }

        System.out.println("Please enter your email address:");
        String email;
        while (!userService.validateEmail(email = consoleReader.readString())) {
            System.out.println("Invalid email format. Must be like 'user@domain.com'.");
        }

        System.out.println("Please enter your password (min 8 chars, 1 uppercase, " +
                "1 lowercase, 1 number):");
        String password;
        while (!userService.validatePassword(password = consoleReader.readString())) {
            System.out.println("Invalid password format. Please ensure it meets the requirements.");
        }

        System.out.println("Please enter your date of birth (DD-MM-YYYY): ");
        String dateOfBirth;
        while (!userService.validateDateOfBirth(dateOfBirth = consoleReader.readString())) {
            System.out.println("Invalid date of birth format. Ensure the format is correct " +
                    "(e.g., 15-08-2010) and that you are at least 13 years old.");
        }

        password = PasswordService.hash(password);
        userService.addUser(username, email, password, dateOfBirth);
        System.out.println("Registration successful! Welcome to our platform.");
    }


    private void createCommunity()  {
        System.out.println("\n--- Create Community ---");
        System.out.print("Please enter community name: \nr/");
        String communityName = "r/" + consoleReader.readString();
        List<String> topics= communityService.getAvailableTopics();

        int choice;
        String selectedTopic;
        System.out.println("TOPICS LIST");
        for(String i:topics){
            System.out.println((topics.indexOf(i) + 1) + ". " + i);
        }

        while (true) {
            System.out.print("Choose an option (1-" + topics.size() + "): ");
            try {
                choice = Integer.parseInt(consoleReader.readString());

                if (choice < 1 || choice > topics.size()) {
                    System.out.println("Invalid option. Please enter a valid number.");
                    continue;
                }

                selectedTopic = topics.get(choice - 1);
                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from the list.");
            }
        }
        System.out.println("Please Enter Community Description");
        String description = consoleReader.readString();


        try {
            communityService.addCommunity(communityName, selectedTopic, description);
            System.out.println("Community " + communityName + " successfully created.");
        } catch (InvalidCommunityException e) {
            System.out.println(e.getMessage());
        }

    }


    private void createPost(){
        System.out.println("Welcome to the post creation page.");
        Community targetCommunity=currentCommunity;
        if (targetCommunity == null) {
            System.out.print("Please enter the community in which you would like to post " +
                    "\n(or press Enter to post to u/" + currentUser.getUsername() + "): r/");

            String input = consoleReader.readString();

            if (input.isEmpty()) {
                System.out.println("Posting to your profile (u/" + currentUser.getUsername() + ").");
            } else {
                String communityName = "r/" + input;
                targetCommunity = communityService.getCommunityByName(communityName);

                if (targetCommunity == null) {
                    System.out.println("Community not found! Posting to your profile instead.");
                }
            }

        }
        System.out.println("Please enter post title:");
        String postTitle = consoleReader.readString();

        System.out.println("Please enter post contents:");
        String postContents = consoleReader.readString();

        System.out.println("Please enter image link (or press Enter to skip):");
        String imageLink = consoleReader.readString();
        if (imageLink.trim().isEmpty()) {
            imageLink = null;
        }
        System.out.println("Is your post NSFW? [yes/no]");
        boolean NSFW = consoleReader.readString().equalsIgnoreCase("yes");
        if(NSFW && !currentUser.checkAge()) {
            System.out.println("You must be at least 18 years old to create an NSFW post.");
            currentState=State.LOGGED_IN;
       }
        else{
            currentPost = postService.addPost(currentUser, postTitle, postContents, imageLink, NSFW, targetCommunity);
            System.out.println("Post created successfully!");
            currentState = State.ON_POST;
        }

    }


    private void printThread(int parentId, Map<Integer, List<Comment>> commentTree, int depth) {

        List<Comment> replies = commentTree.get(parentId);

        if (replies != null) {
            for (Comment reply : replies) {

                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";


                System.out.println(indent + branch + "[" + reply.getUsername() + "]: " + reply.getText() + "  (ID: " + reply.getId() + ")");

                printThread(reply.getId(), commentTree, depth + 1);
            }
        }
    }

    private void showCommentsThread() {
        List<Comment> flatCommentsList = currentPost.getComments();
        if (flatCommentsList == null || flatCommentsList.isEmpty()) {
            System.out.println("\n(No comments yet. Be the first to reply!)");
            return;
        }

        System.out.println("\n--- Discussion Thread ---");

        Map<Integer, List<Comment>> commentTree = new HashMap<>();
        for (Comment comment : flatCommentsList) {
            commentTree.putIfAbsent(comment.getIdParent(), new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        printThread(-1, commentTree, 0);

        System.out.print("\nPress Enter to return to the post menu...");
        consoleReader.readString();
    }

    private void writeCommentToPost() {
        while(true) {
            System.out.println("Write Comment: ");
            String text = consoleReader.readString();
            try {
                commentService.addComment(currentUser, currentPost, text);
                System.out.println("Comment added successfully!");
                break;
            } catch (EmptyCommentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void selectCommentToReply() {
        System.out.print("Enter Comment ID to select: ");
        try {
            int commentId = Integer.parseInt(consoleReader.readString());
            Comment foundComment = currentPost.findCommentById(commentId);

            if (foundComment != null) {
                currentComment = foundComment;
                currentState = State.ON_COMMENT;
            } else {
                System.out.println("Comment not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }
}
