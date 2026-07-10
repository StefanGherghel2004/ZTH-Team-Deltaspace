package cli.backend.commands.mainmenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;

public class ShowCommunitiesCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        ConsoleReader reader = ConsoleReader.getInstance();

        System.out.println("\n--- Communities ---");
        if (communityService.getCommunities().isEmpty()) {
            System.out.println("No communities created.");
            return true;
        }

        for (Community c : communityService.getCommunities()) {
            if(c.hasNSFWPost() && !app.getCurrentUser().checkAge()){
                continue;
            }
            System.out.println(c.getNickname());

        }

        System.out.print("\nChoose a community (or press Enter to go back): ");
        String communityName = reader.readString();

        if (!communityName.isEmpty()) {
            Community foundCommunity = communityService.getCommunityByName(communityName);
            if (foundCommunity != null) {
                app.setCurrentCommunity(foundCommunity);
                app.setCurrentState(AppHandler.State.ON_COMMUNITY);
            } else {
                System.out.println("Community not found!");
            }
        }
        return true;
    }
}