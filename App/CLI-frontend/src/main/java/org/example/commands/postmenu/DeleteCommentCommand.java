package org.example.commands.postmenu;


import org.example.Comment;
import org.example.Post;
import org.example.apiclients.CommentApiClient;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

public class DeleteCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Comment currentComment = appHandler.getCurrentComment();
        Post currentPost = appHandler.getCurrentPost();
        Console console = Console.getInstance();
        CommentApiClient commentApiClient = CommentApiClient.getInstance();

        if(currentComment == null)
            return false;

        boolean confirm = console.getUserConfirmation("Are you sure you want to delete this comment? (yes/no): ");

        if (confirm) {

            boolean removed = commentApiClient.deleteCommentById(currentComment.getId(), appHandler.getJwtToken());

            if (removed) {

                appHandler.setCurrentComment(null);
                appHandler.setCurrentState(AppHandler.State.ON_POST);
                console.success("Comment deleted successfully!");
                return true;
            }
        }else{

            console.info("Comment deletion cancelled");
            appHandler.setCurrentState(AppHandler.State.ON_COMMENT);
        }
        return true;
    }
}
