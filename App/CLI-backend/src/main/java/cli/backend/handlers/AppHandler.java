package cli.backend.handlers;

import java.util.Scanner;

public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN
    }

    private State currentState = State.NOT_LOGGED_IN;

    private static AppHandler instance;

    private static Scanner sc = new Scanner(System.in);

    private static UserHandler userHandler = UserHandler.getInstance();

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
                System.out.println("1. Register\n2. Login\n3. Shutdown");
                break;
            case LOGGED_IN:
                System.out.println("\n1. Show feed\n2. Create community\n3. Logout");
                break;
        }

        System.out.print("Choose an option: ");
        int command = Integer.parseInt(sc.nextLine());

        if (currentState == State.NOT_LOGGED_IN) {
            switch(command) {
                case 1:
                    userHandler.userRegister();
                    break;
                case 2:
                    if (userHandler.userLogin() != null) {
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
                    System.out.println("Showing feed...");
                    break;
                case 2:
                    System.out.println("Creating community...");
                    break;
                case 3:
                    System.out.println("Logging out...");
                    currentState = State.NOT_LOGGED_IN;
                    break;
            }
        }

        return true;

    }
}
