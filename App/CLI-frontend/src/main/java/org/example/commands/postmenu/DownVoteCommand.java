package org.example.commands.postmenu;

import org.example.Post;
import org.example.apiclients.PostApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class DownVoteCommand implements Command {

    private final PostApiClient postApiClient = PostApiClient.getInstance();

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Post currentPost = app.getCurrentPost();

        boolean success = postApiClient.votePost(currentPost, "down", app.getJwtToken());

        if (!success) {
            console.error("Failed to register vote on the server.");
        }
        return true;

    }
}
