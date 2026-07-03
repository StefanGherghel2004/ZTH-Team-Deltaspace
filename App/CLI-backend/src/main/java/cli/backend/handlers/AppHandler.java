package cli.backend.handlers;

import java.util.Scanner;

public class AppHandler {

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
        System.out.println("Welcome to Deltaspace platform");
        System.out.println("Press one of the following keys to proceed");
        System.out.println("1. Register\n2. Login\n3. Shutdown");

        // some function to handle the UI maybe moved later in another class
        boolean isActive = true;

        while(isActive) {
            isActive = handleState();
        }
    }

    private boolean handleState() {

        int command = Integer.parseInt(sc.nextLine());

        switch(command) {
            case 1:
                userHandler.userRegister();
                break;
            case 2:
                break;
            case 3:
                return false;

        }

        return true;

    }
}
