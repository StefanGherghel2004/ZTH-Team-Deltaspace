package cli.backend.commands.mainmenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommunityService;

public class ShowCommunitiesCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        Console console = Console.getInstance();

        console.info("\n--- Communities ---");
        if (communityService.getCommunities().isEmpty()) {
            console.info("No communities created.");
            return true;
        }

        for (Community c : communityService.getCommunities()) {
            if(c.hasNSFWPost() && !app.getCurrentUser().checkAge()){
                continue;
            }
            console.info(c.getNickname());
        }

        String communityName = console.getStringInput("Choose a community (or press Enter to go back): ", true);

        if (!communityName.isEmpty()) {
            Community foundCommunity = communityService.getCommunityByName(communityName);
            if (foundCommunity != null) {
                app.setCurrentCommunity(foundCommunity);
                app.setCurrentState(AppHandler.State.ON_COMMUNITY);
            } else {
                console.error("Community not found!");
            }
        }
        return true;
    }
}