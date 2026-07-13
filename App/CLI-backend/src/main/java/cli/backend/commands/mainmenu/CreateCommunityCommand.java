package cli.backend.commands.mainmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.InvalidCommunityException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;
import java.util.List;

public class CreateCommunityCommand implements Command {
    @Override
    public boolean execute() {
        CommunityService communityService = CommunityService.getInstance();
        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();

        console.info("\n--- Create Community ---");
        String communityName = "r/" + console.getStringInput("Please enter community name: \nr/");
        List<String> topics = communityService.getAvailableTopics();

        console.info("TOPICS LIST");
        for (String i : topics) {
            console.info((topics.indexOf(i) + 1) + ". " + i);
        }

        int choice = console.getIntInRangeInput(1, topics.size(), "");
        String selectedTopic = topics.get(choice);

        String description = console.getStringInput("Please Enter Community Description");

        try {
            communityService.addCommunity(appHandler.getCurrentUser(), communityName, selectedTopic, description);
            console.success("Community " + communityName + " successfully created.");
            appHandler.setCurrentCommunity(communityService.getCommunityByName(communityName));
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        } catch (InvalidCommunityException e) {
            console.error(e.getMessage());
        }
        return true;
    }
}