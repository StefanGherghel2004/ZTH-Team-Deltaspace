package cli.backend.commands.mainmenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommunityService;
import cli.backend.userinterface.views.UICommunity;

import java.util.List;

public class ShowCommunitiesCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        Console console = Console.getInstance();
        UICommunity uiCommunity = UICommunity.getInstance();
        List<Community> communities = communityService.getCommunities();

        uiCommunity.showCommunitiesList(communities, app.getCurrentUser());

        if (communities.isEmpty()) {
            return true;
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