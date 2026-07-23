package cli.backend.userinterface.views;

import cli.backend.Comment;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cli.backend.userinterface.textformatters.Theme.MAX_TEXT_WIDTH;

public class UIComment {

    private static UIComment instance;
    private final Console console;

    private UIComment() {
        this.console = Console.getInstance();
    }

    public static UIComment getInstance() {
        if (instance == null) {
            instance = new UIComment();
        }
        return instance;
    }

    public void showCommentTree(Map<Long, List<Comment>> commentTree) {
        if (commentTree == null || commentTree.isEmpty()) {
            console.info("(No comments yet. Be the first to reply!)");
            return;
        }

        console.info("\n--- Discussion Thread ---");

        printThread(0L, commentTree, 0);
    }

    private void printThread(Long parentId, Map<Long, List<Comment>> commentTree, int depth) {
        List<Comment> replies = commentTree.get(parentId);
        if (replies != null) {
            for (Comment reply : replies) {
                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";
                console.info(indent + branch + "[" + reply.getAuthorUsername() + "]: "
                        + reply.getText() + "  (ID: " + reply.getId() + ")");
                printThread(reply.getId(), commentTree, depth + 1);
            }
        }
    }

    public void showComment(Comment c) {

        String title = c.getAuthorUsername();

        List<String> wrappedContent = TextWrapper.wrap(c.getText(), MAX_TEXT_WIDTH);
        List<String> lines = new ArrayList<>(wrappedContent);

        String boxedPost = BoxPadder.format(lines, title);
        console.info(boxedPost);
    }
}
