package cli.backend.handlers;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.exceptions.InvalidUserAccountException;
import cli.backend.services.CommentService;
import cli.backend.services.PasswordService;
import cli.backend.services.UserService;

import java.util.Scanner;

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

    private static Scanner sc = new Scanner(System.in);

    private static final PostHandler postHandler = PostHandler.getInstance();
    private static final CommunityHandler communityHandler=CommunityHandler.getInstance();
    private static final UserService userService = UserService.getInstance();
    private static final CommentService commentService = CommentService.getInstance();

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
        System.out.println("\n--- Welcome to Deltaspace platform ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Quit");

        int command = readIntegerCommand(1, 3);

        switch(command) {
            case 1:
                System.out.println("Welcome to the registration page.");
                System.out.println("Please enter your username (4-20 characters, alphanumeric):");
                String username;
                while (!userService.validateUsername(username = sc.nextLine())){
                    System.out.println("Invalid username format. Please try again.");
                }

                System.out.println("Please enter your email address:");
                String email;
                while (!userService.validateEmail(email = sc.nextLine())) {
                    System.out.println("Invalid email format. Must be like 'user@domain.com'.");
                }

                System.out.println("Please enter your password (min 8 chars, 1 uppercase, " +
                        "1 lowercase, 1 number):");
                String password;
                while (!userService.validatePassword(password = sc.nextLine())) {
                    System.out.println("Invalid password format. Please ensure it meets the requirements.");
                }

                System.out.println("Please enter your date of birth (DD-MM-YYYY): ");
                String dateOfBirth;
                while (!userService.validateDateOfBirth(dateOfBirth = sc.nextLine())) {
                    System.out.println("Invalid date of birth format. Ensure the format is correct " +
                            "(e.g., 15-08-2010) and that you are at least 13 years old.");
                }

                password = PasswordService.hash(password);
                userService.addUser(username, email, password, dateOfBirth);
                System.out.println("Registration successful! Welcome to our platform.");

                break;
            case 2:
                User loggedInUser;
                System.out.println("Welcome to the login page.");
                while (true) {
                    System.out.println("Insert your username:");
                    String loginUsername = sc.nextLine();

                    System.out.println("Insert your password:");
                    String loginPassword = sc.nextLine();

                    try{
                        loggedInUser = userService.validateUserAccount(loginUsername,loginPassword);
                        System.out.println("Successfully logged in into your account - "
                                           + loggedInUser.getUsername());
                        break;
                    } catch (InvalidUserAccountException e){
                        System.out.println(e.getMessage());
                    }
                }

                currentUser = loggedInUser;
                currentState = State.LOGGED_IN;
                break;
            case 3:
                System.out.println("Shutting down...");
                return false;
        }
        return true;
    }

    private boolean handleLoggedIn() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Show feed");
        System.out.println("2. Create community");
        System.out.println("3. Create Post");
        System.out.println("4. Show communities");
        System.out.println("5. Logout");

        int command = readIntegerCommand(1, 5);

        switch(command) {
            case 1:
                currentState = State.SHOW_FEED;
                break;
            case 2:
                communityHandler.addCommunity();
                break;
            case 3:
                postHandler.addPost(currentUser, null);
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

    private boolean handleShowCommunities() {
        System.out.println("\n--- Communities ---");
        communityHandler.viewCommunities();

        if (communityHandler.getCommunities().isEmpty()) {
            currentState = State.LOGGED_IN;
            return true;
        }

        System.out.print("\nChoose a community (or press Enter to go back): ");
        String communityName = sc.nextLine().trim();

        if (communityName.isEmpty()) {
            currentState = State.LOGGED_IN;
            return true;
        }

        Community foundCommunity = communityHandler.findCommunityByName(communityName);
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

        int command = readIntegerCommand(1, 3);

        switch(command){
            case 1:
                currentState = State.SHOW_POSTS_COMMUNITY;
                break;
            case 2:
                postHandler.addPost(currentUser, currentCommunity);
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
        communityHandler.viewCommunityPosts(currentCommunity);

        if(currentCommunity.getPosts().isEmpty()){
            currentState = State.IN_COMMUNITY;
            return true;
        }

        System.out.print("Choose a post [ID] (or press Enter to go back): ");
        String input = sc.nextLine().trim();

        if (input.isEmpty()) {
            currentState = State.IN_COMMUNITY;
            return true;
        }

        try {
            int id = Integer.parseInt(input);
            currentPost = currentCommunity.findPostById(id);

            if(currentPost != null){
                currentState = State.ON_POST;
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
        postHandler.viewPost(currentPost);

        System.out.println("1. Show comments");
        System.out.println("2. Add comment");
        System.out.println("3. Select comment (Reply)");
        System.out.println("4. Back to Community");

        int command = readIntegerCommand(1, 4);

        switch(command){
            case 1:
                postHandler.showComments(currentPost);
                System.out.print("\nPress Enter to return to the post menu...");
                sc.nextLine();
                break;
            case 2:
                while(true) {
                    System.out.println("Write Comment: ");
                    String text = sc.nextLine();
                    try {
                        commentService.addComment(currentUser, currentPost, text);
                        System.out.println("Comment added successfully!");
                        break;
                    } catch (EmptyCommentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 3:
                System.out.print("Enter Comment ID to select: ");
                try {
                    int commentId = Integer.parseInt(sc.nextLine().trim());
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
                break;
            case 4:
                currentPost = null;
                currentState = State.IN_COMMUNITY;
                break;
        }
        return true;
    }

    private boolean handleOnComment() {
        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());

        System.out.println("\n1. Reply");
        System.out.println("2. Back to Post");

        int command = readIntegerCommand(1, 2);

        switch(command) {
            case 1:
                System.out.print("Write reply: ");
                String text = sc.nextLine().trim();

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
        postHandler.viewFeed();

        if(postHandler.getPosts().isEmpty()){
            System.out.println(postHandler.getPosts());
            System.out.print("\nPress Enter to return to Main Menu...");
            sc.nextLine();
            currentState = State.LOGGED_IN;
            return true;
        }

        System.out.print("Choose a post [ID] (or press Enter to go back): ");
        String input = sc.nextLine().trim();

        if (input.isEmpty()) {
            currentState = State.LOGGED_IN;
            return true;
        }

        try {
            int id = Integer.parseInt(input);

            Post foundPost = postHandler.findPostById(id);

            if(foundPost != null){
                currentPost = foundPost;
                currentState = State.ON_POST;
            } else {
                System.out.println("Post not found in feed!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }

        return true;
    }

    // method will return just after introducing a valid option in the range
    private int readIntegerCommand(int min, int max) {
        while (true) {

            System.out.print("Choose an option (" + min + "-" + max + "): ");

            try {
                int command = Integer.parseInt(sc.nextLine().trim());

                if (command >= min && command <= max) {
                    return command;
                } else {
                    System.out.println("Invalid option. Please enter a number between" +
                                       " " + min + " and " + max + ".");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
