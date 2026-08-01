package org.example.commands.commentmenu;

import org.example.Comment;
import org.example.apiclients.CommentApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class DownVoteCommentCommand implements Command {
    private final CommentApiClient commentApiClient = CommentApiClient.getInstance();

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Comment currentComment = app.getCurrentComment();

        boolean success = commentApiClient.voteComment(currentComment, "down", app.getJwtToken());

        if (!success) {
            console.error("Failed to register vote on the server.");
        }

        return true;
    }
}
