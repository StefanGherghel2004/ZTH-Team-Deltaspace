package org.example.commands.postmenu;

import org.example.Comment;
import org.example.apiclients.CommentApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.exceptions.EmptyCommentException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

import java.util.Map;

public class ReplyToCommentCommand implements Command {
    @Override
    public boolean execute() {

        AppHandler appHandler = AppHandler.getInstance();
        Console console = Console.getInstance();
        CommentApiClient commentApiClient = CommentApiClient.getInstance();

        try {
            String text = console.getMultiLineInput("Write reply");

            try {
                if (text == null || text.trim().isEmpty())
                    throw new EmptyCommentException("Error! Comment text cannot be empty.");
                Map<String,Object> payload = Map.of(
                        "text",text,
                        "postId", appHandler.getCurrentPost().getId(),
                        "parentCommentId", appHandler.getCurrentComment().getId()
                );
                Comment createdComment = commentApiClient.addComment(payload, appHandler.getJwtToken());

                if (createdComment != null) {
                    console.success("Reply added successfully!");
                }
            } catch (EmptyCommentException e) {
                console.error(e.getMessage());
            }
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        return true;

    }
}