package cli.backend.handlers;

import cli.backend.User;

import java.util.Scanner;

public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN
    }

    private State currentState = State.NOT_LOGGED_IN;
    private User currentUser;

    private static AppHandler instance;

    private static Scanner sc = new Scanner(System.in);

    private static UserHandler userHandler = UserHandler.getInstance();
    private static PostHandler postHandler = PostHandler.getInstance();
    private static CommunityHandler communityHandler=CommunityHandler.getInstance();

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

        switch(currentState) {
            case NOT_LOGGED_IN:
                System.out.println("\nWelcome to Deltaspace platform");
                System.out.println("1. Register\n2. Login\n3. Quit");
                break;
            case LOGGED_IN:
                System.out.println("\n1. Show feed\n2. Create community\n3. Create Post\n4. Show communities\n5. Logout");
                break;
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
                    communityHandler.viewCommunities();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    currentUser = null;
                    currentState = State.NOT_LOGGED_IN;
                    break;
            }
        }

        return true;

    }
}
