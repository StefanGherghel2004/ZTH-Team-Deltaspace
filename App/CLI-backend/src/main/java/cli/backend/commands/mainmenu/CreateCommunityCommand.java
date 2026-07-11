package cli.backend.commands.mainmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.InvalidCommunityException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;
import java.util.List;

public class CreateCommunityCommand implements Command {
    @Override
    public boolean execute() {
        ConsoleReader consoleReader = ConsoleReader.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        AppHandler appHandler = AppHandler.getInstance();

        System.out.println("\n--- Create Community ---");
        System.out.print("Please enter community name: \nr/");
        String communityName = "r/" + consoleReader.readString();
        List<String> topics = communityService.getAvailableTopics();

        System.out.println("TOPICS LIST");
        for (String i : topics) {
            System.out.println((topics.indexOf(i) + 1) + ". " + i);
        }

        int choice;
        String selectedTopic;
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
            communityService.addCommunity(appHandler.getCurrentUser(), communityName, selectedTopic, description);
            System.out.println("Community " + communityName + " successfully created.");
            appHandler.setCurrentCommunity(communityService.getCommunityByName(communityName));
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        } catch (InvalidCommunityException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}