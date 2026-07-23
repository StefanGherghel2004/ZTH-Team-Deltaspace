package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommentService;
import cli.backend.userinterface.views.UIComment;

import java.util.List;
import java.util.Map;

public class ShowCommentsCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Post currentPost = app.getCurrentPost();

        Map<Long, List<Comment>> commentTree = CommentService.getInstance().getCommentTree(currentPost);

        UIComment.getInstance().showCommentTree(commentTree);

        console.getStringInput("Press Enter to return to the post menu...", true);
        return true;
    }

}