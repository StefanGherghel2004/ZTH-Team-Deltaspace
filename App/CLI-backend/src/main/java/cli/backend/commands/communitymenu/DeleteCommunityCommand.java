package cli.backend.commands.communitymenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommunityService;


public class DeleteCommunityCommand implements Command {


    @Override
    public boolean execute() {

        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        CommunityService communityService = CommunityService.getInstance();
        Console console = Console.getInstance();

        if(currentCommunity == null)
            return false;

        boolean confirmation = console.getUserConfirmation("Are you sure you want to delete this community? (yes/no): ");

        if (confirmation) {
            boolean removed = communityService.deleteCommunity(currentCommunity);

            if (removed) {
                appHandler.setCurrentCommunity(null);
                appHandler.setCurrentState(AppHandler.State.LOGGED_IN);
                console.success("Community deleted successfully!");
                return true;
            }
        }else{

            console.info("Community deletion cancelled.");
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        }
        return true;
    }
}
