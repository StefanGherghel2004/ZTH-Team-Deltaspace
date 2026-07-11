package cli.backend.commands.communitymenu;

import cli.backend.Community;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.ConsoleLogger;
import cli.backend.loggers.LogLevel;
import cli.backend.loggers.Logger;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;

import java.util.List;


public class DeleteCommunityCommand implements Command {


    @Override
    public boolean execute() {

        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        CommunityService communityService = CommunityService.getInstance();
        List<Community> communityList = communityService.getCommunities();
        ConsoleLogger consoleLogger = new ConsoleLogger(LogLevel.INFO);
        ConsoleReader consoleReader = new ConsoleReader();

        if(currentCommunity == null)
            return false;

        System.out.print("Are you sure you want to delete this community? (yes/no): ");
        String confirmation = consoleReader.readString();

        if (confirmation.equalsIgnoreCase("yes")) {

            boolean removed = communityList.removeIf(c -> c.equals(currentCommunity));

            if (removed) {

                appHandler.setCurrentCommunity(null);
                appHandler.setCurrentState(AppHandler.State.LOGGED_IN);
                consoleLogger.log(LogLevel.INFO, "Community deleted successfully!");
                return true;
            }
        }else{

            System.out.println("Community deletion cancelled.");
            appHandler.setCurrentState(AppHandler.State.ON_COMMUNITY);
        }
        return false;
    }
}
