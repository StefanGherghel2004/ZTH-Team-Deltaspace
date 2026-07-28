package org.example.commands.postmenu;

import org.example.Community;
import org.example.Post;
import org.example.apiclients.PostApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class DeletePostCommand implements Command {

    private final PostApiClient postApiClient = PostApiClient.getInstance();

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        Post postToDelete = app.getCurrentPost();
        Community currentCommunity = app.getCurrentCommunity();

        boolean confirm = console.getUserConfirmation("Are you sure you want to delete this post? (yes/no): ");

        if (!confirm) {
            console.info("Post deletion cancelled.");
            app.setCurrentState(AppHandler.State.ON_POST);
            return true;
        }

        boolean success = postApiClient.deletePost(postToDelete.getId(), app.getJwtToken());

        if (success) {
            app.setCurrentPost(null);
            returnToPreviousState(app, currentCommunity);
            console.success("Post deleted successfully.");
        } else {
            console.error("Failed to delete the post.");
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