package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.ConsoleLogger;
import cli.backend.loggers.LogLevel;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommentService;


public class DeleteCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        ConsoleLogger consoleLogger = new ConsoleLogger(LogLevel.INFO);
        Comment currentComment = appHandler.getCurrentComment();
        Post currentPost = appHandler.getCurrentPost();
        CommentService commentService = CommentService.getInstance();
        ConsoleReader consoleReader = new ConsoleReader();

        if(currentComment == null)
            return false;

        System.out.print("Are you sure you want to delete this comment? (yes/no): ");
        String confirmation = consoleReader.readString();

        if (confirmation.equalsIgnoreCase("yes")) {

            boolean removed = commentService.deleteComment(currentPost, currentComment);

            if (removed) {

                appHandler.setCurrentComment(null);
                appHandler.setCurrentState(AppHandler.State.ON_POST);
                consoleLogger.log(LogLevel.INFO, "Comment deleted successfully!");
                return true;
            }
        }else{

            System.out.println("Comment deletion cancelled.");
            appHandler.setCurrentState(AppHandler.State.ON_COMMENT);
        }
        return true;
    }
}
