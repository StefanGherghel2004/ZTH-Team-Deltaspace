package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommentService;


public class DeleteCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        Comment currentComment = appHandler.getCurrentComment();
        Post currentPost = appHandler.getCurrentPost();
        CommentService commentService = CommentService.getInstance();
        Console console = Console.getInstance();

        if(currentComment == null)
            return false;

        boolean confirm = console.getUserConfirmation("Are you sure you want to delete this comment? (yes/no): ");

        if (confirm) {

            boolean removed = commentService.deleteComment(currentComment);

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
