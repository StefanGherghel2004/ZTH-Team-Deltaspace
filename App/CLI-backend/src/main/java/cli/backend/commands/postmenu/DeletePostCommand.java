package cli.backend.commands.postmenu;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.PostService;

public class DeletePostCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        PostService postService = PostService.getInstance();

        Post postToDelete = app.getCurrentPost();
        Community currentCommunity = app.getCurrentCommunity();

        if (!postService.canUserDeletePost(app.getCurrentUser(), postToDelete, currentCommunity)) {
            console.error("You cannot delete this post as you are not the owner.");
            returnToPreviousState(app, currentCommunity);
            return true;
        }

        boolean confirm = console.getUserConfirmation("Are you sure you want to delete this post? (yes/no): ");

        if (confirm) {
            postService.deletePost(postToDelete);
            app.setCurrentPost(null);
            returnToPreviousState(app, currentCommunity);
            console.success("Post deleted successfully.");
        } else {
            console.info("Post deletion cancelled.");
            app.setCurrentState(AppHandler.State.ON_POST);
        }

        return true;

    }

    private void returnToPreviousState(AppHandler app, Community currentCommunity) {
        if (currentCommunity != null) {
            app.setCurrentState(AppHandler.State.ON_COMMUNITY);
        } else {
            app.setCurrentState(AppHandler.State.LOGGED_IN);
        }
    }

}
