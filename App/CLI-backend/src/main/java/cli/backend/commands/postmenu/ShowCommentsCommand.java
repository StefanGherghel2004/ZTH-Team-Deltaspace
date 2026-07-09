package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowCommentsCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        List<Comment> flatCommentsList = app.getCurrentPost().getComments();

        if (flatCommentsList == null || flatCommentsList.isEmpty()) {
            System.out.println("\n(No comments yet. Be the first to reply!)");
            return true;
        }

        System.out.println("\n--- Discussion Thread ---");
        Map<Integer, List<Comment>> commentTree = new HashMap<>();
        for (Comment comment : flatCommentsList) {
            commentTree.putIfAbsent(comment.getIdParent(), new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        printThread(-1, commentTree, 0);

        System.out.print("\nPress Enter to return to the post menu...");
        ConsoleReader.getInstance().readString();
        return true;
    }

    private void printThread(int parentId, Map<Integer, List<Comment>> commentTree, int depth) {
        List<Comment> replies = commentTree.get(parentId);
        if (replies != null) {
            for (Comment reply : replies) {
                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";
                System.out.println(indent + branch + "[" + reply.getUsername() + "]: " + reply.getText() + "  (ID: " + reply.getId() + ")");
                printThread(reply.getId(), commentTree, depth + 1);
            }
        }
    }
}