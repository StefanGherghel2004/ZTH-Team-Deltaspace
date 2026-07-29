package org.example.userinterface.views;

import org.example.Comment;
import org.example.userinterface.readers.Console;
import org.example.userinterface.textformatters.BoxPadder;
import org.example.userinterface.textformatters.TextWrapper;
import org.example.userinterface.textformatters.Theme;

import java.util.*;

import static org.example.userinterface.textformatters.Theme.*;

public class UIComment {

    private static final String NO_COMMENTS = "(No comments yet. Be the first to reply!)";
    private static final String HEADER_TITLE = "Discussion Thread";
    private static final int MAX_PREVIEW_LENGTH = 40;
    private final Map<Integer,Comment> indexedComment = new HashMap<>();
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

    public void showCommentTree(Map<UUID, List<Comment>> commentTree) {
        indexedComment.clear();
        if (commentTree == null || commentTree.isEmpty()) {
            console.info(NO_COMMENTS);
            return;
        }

        String header = header(HEADER_TITLE);
        String footer = footer();

        console.info(header);
        printThread(new UUID(0L,0L), commentTree, 0,new int[]{1});
        console.info(footer);
    }

    private void printThread(UUID parentId, Map<UUID, List<Comment>> commentTree, int depth, int[] indexCounter) {
        List<Comment> replies = commentTree.get(parentId);
        if (replies != null) {
            for (Comment reply : replies) {
                int currentIndex=indexCounter[0]++;
                indexedComment.put(currentIndex,reply);
                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";

                // replace newline with space for oneline text
                String safeText = reply.getText().replace("\n", " ");

                if (safeText.length() > MAX_PREVIEW_LENGTH) {
                    safeText = safeText.substring(0, MAX_PREVIEW_LENGTH) + "...";
                }

                console.info(indent + branch + "[" + currentIndex + "] ["+
                        Theme.formatUsername(reply.getAuthorUsername()) + "]: "
                        + safeText + "  (ID: " + reply.getId() + ")");

                printThread(reply.getId(), commentTree, depth + 1,indexCounter);
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
