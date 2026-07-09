package cli.backend.userinterface;

public class MainMenu extends Menu{

    @Override
    public void showMenu () {

        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Show feed");
        System.out.println("2. Create community");
        System.out.println("3. Create Post");
        System.out.println("4. Show communities");
        System.out.println("5. Logout");
    }
}
