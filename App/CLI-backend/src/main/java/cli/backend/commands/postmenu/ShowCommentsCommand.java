package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.services.CommentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowCommentsCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Post currentPost = app.getCurrentPost();

        Map<Integer, List<Comment>> commentTree = CommentService.getInstance().getCommentTree(currentPost);

        if (commentTree.isEmpty()) {
            console.info("(No comments yet. Be the first to reply!)");
            return true;
        }

        console.info("\n--- Discussion Thread ---");
        printThread(-1, commentTree, 0, console);

        console.getStringInput("Press Enter to return to the post menu...", true);
        return true;
    }

    private void printThread(int parentId, Map<Integer, List<Comment>> commentTree, int depth, Console console) {
        List<Comment> replies = commentTree.get(parentId);
        if (replies != null) {
            for (Comment reply : replies) {
                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";
                console.info(indent + branch + "[" + reply.getUsername() + "]: " + reply.getText() + "  (ID: " + reply.getId() + ")");
                printThread(reply.getId(), commentTree, depth + 1, console);
            }
        }
    }
}