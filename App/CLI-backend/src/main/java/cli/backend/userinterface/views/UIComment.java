package cli.backend.userinterface.views;

import cli.backend.Comment;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.TextWrapper;
import cli.backend.userinterface.textformatters.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cli.backend.userinterface.textformatters.Theme.MAX_TEXT_WIDTH;

public class UIComment {

    private static final int MAX_PREVIEW_LENGTH = 40;

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

                // replace newline with space for oneline text
                String safeText = reply.getText().replace("\n", " ");

                if (safeText.length() > MAX_PREVIEW_LENGTH) {
                    safeText = safeText.substring(0, MAX_PREVIEW_LENGTH) + "...";
                }

                console.info(indent + branch + "[" +
                        Theme.formatUsername(reply.getAuthorUsername()) + "]: "
                        + safeText + "  (ID: " + reply.getId() + ")");

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
