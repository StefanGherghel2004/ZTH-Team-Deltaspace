package org.example.userinterface.views;

import org.example.Post;
import org.example.User;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.example.userinterface.textformatters.BoxPadder;
import org.example.userinterface.textformatters.Color;
import org.example.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.userinterface.textformatters.Theme.*;

public class UIPost {

    // constants for UI
    private static final String NO_POSTS_COMMUNITY = "No posts in this r/. Be the first to post!";
    private static final String NO_POSTS_GLOBAL = "No posts yet. Create the first one!";

    private static final String TITLE_COMMUNITY_FEED = "Posts in %s";
    private static final String TITLE_GLOBAL_FEED = "Global Feed";

    // this limits the length of the fields for a standard length
    private static final String FORMAT_POST_SIMPLE = "[%2d] | Title: %-12.12s | Author: %-10.10s | %s";

    // symbols
    private static final String UPVOTE_SYMBOL = "▲ ";
    private static final String DOWNVOTE_SYMBOL = "▼ ";
    private static final String IMAGE_SYMBOL = "[IMG]";

    private static UIPost instance;

    private final Console console;

    private UIPost() {
        this.console = Console.getInstance();
    }

    public static UIPost getInstance() {
        if (instance == null) {
            instance = new UIPost();
        }
        return instance;
    }

    public void showFeed(List<Post> posts) {
        showFeed(posts, null);
    }

    public void showFeed(List<Post> posts, String communityName) {
        if (communityName != null) {
            String title = String.format(TITLE_COMMUNITY_FEED, communityName);
            console.info(header(title));
        } else {
            console.info(header(TITLE_GLOBAL_FEED));
        }

        if (posts.isEmpty()) {
            if (communityName != null) {
                console.info(NO_POSTS_COMMUNITY);
            } else {
                console.info(NO_POSTS_GLOBAL);
            }
            return;
        }

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            int displayIndex = i + 1;
            showPostSimple(post, displayIndex);
        }

        console.info(footer());
    }

    public void showPostSimple(Post post, int displayIndex) {
        String vote = post.getUserVote();
        String formattedVotes = formatVotes(post);


        String postLine = String.format(FORMAT_POST_SIMPLE,
                displayIndex,
                post.getTitle(),
                post.getAuthor(),
                formattedVotes);

        console.info(postLine);
    }

    public void showPostExpanded(Post post) {
        String title = post.getTitle();
        List<String> lines = new ArrayList<>();

        lines.add("Author: " + post.getAuthor());
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(post.getContent(), MAX_TEXT_WIDTH);
        lines.addAll(wrappedContent);

        if (post.getImageUrl() != null) {
            lines.add("");
            lines.add(IMAGE_SYMBOL);
        }

        if (post.getSubreddit() != null) {
            lines.add("");
            lines.add("r/" + post.getSubreddit());
        }

        lines.add("");
        lines.add(formatVotes(post));

        String boxedPost = BoxPadder.format(lines, title);
        console.info(boxedPost);
    }

    private String formatVotes(Post post) {
        String upVoteStr = UPVOTE_SYMBOL + post.getUpvotes();
        String downVoteStr = DOWNVOTE_SYMBOL + post.getDownvotes();

        String vote = post.getUserVote();

        if (vote != null) {
            if (vote.equals("up")) {
                upVoteStr = Color.textGreen(upVoteStr);
            } else if (vote.equals("down")) {
                downVoteStr = Color.textRed(downVoteStr);
            }
        }

        return upVoteStr + " | " + downVoteStr;
    }

}