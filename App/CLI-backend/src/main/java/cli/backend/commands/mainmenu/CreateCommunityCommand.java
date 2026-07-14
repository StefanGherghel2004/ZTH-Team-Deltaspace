package cli.backend.commands.mainmenu;

import cli.backend.commands.Command;
import cli.backend.database.ExcelRead;
import cli.backend.duplicates.CheckDuplicate;
import cli.backend.duplicates.CommunityDuplicate;
import cli.backend.exceptions.InvalidCommunityException;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.ConsoleLogger;
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
        CheckDuplicate communityCheck = new CommunityDuplicate();

        console.info("\n--- Create Community ---");
        String communityName;
        while(true){
            communityName = console.getStringInput("Please enter community name:");
            if (!communityName.startsWith("r/")) {
                communityName = "r/" + communityName;
            }
            if(communityCheck.isDuplicate(communityName)){
                console.error("Community name already exists. Please choose a different name.");
                continue;
            }
            break;
        }
        List<String> topics = communityService.getAvailableTopics();
        console.printIndexList("Topics", topics);

        int choice = console.getIntInRangeInput(1, topics.size(), "");
        String selectedTopic = topics.get(choice - 1);

        String description = console.getStringInput("Please Enter Community Description");

        try {
            communityService.addCommunity(appHandler.getCurrentUser().getUsername(), communityName, selectedTopic, description);
            console.success("Community " + communityName + " successfully created.");
            appHandler.setCurrentCommunity(communityService.getCommunityByName(communityName));
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        } catch (InvalidCommunityException e) {
            console.error(e.getMessage());
        }
        return true;
    }
}