package cli.backend.commands.communitymenu;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.ConsoleLogger;
import cli.backend.loggers.LogLevel;
import cli.backend.loggers.Logger;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;
import cli.backend.services.PostService;

import java.util.List;


public class DeleteCommunityCommand implements Command {


    @Override
    public boolean execute() {

        AppHandler appHandler = AppHandler.getInstance();
        Community currentCommunity = appHandler.getCurrentCommunity();
        CommunityService communityService = CommunityService.getInstance();
        List<Community> communityList = communityService.getCommunities();
        Console console = Console.getInstance();

        PostService postService = PostService.getInstance();
        if(currentCommunity == null)
            return false;

        boolean confirmation = console.getUserConfirmation("Are you sure you want to delete this community? (yes/no): ");

        if (confirmation) {

            boolean removed = communityList.removeIf(c -> c.equals(currentCommunity));
            List<Post> poststodelete=appHandler.getCurrentCommunity().getPosts();

            if (removed) {
                for (Post p : poststodelete)
                    postService.deletePost(p);
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
