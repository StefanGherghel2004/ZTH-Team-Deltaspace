package cli.backend.userinterface.views;


import cli.backend.Post;
import cli.backend.User;
import cli.backend.handlers.AppHandler;
import cli.backend.services.UserService;
import cli.backend.services.VoteService;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.Color;
import cli.backend.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cli.backend.userinterface.textformatters.Theme.MAX_TEXT_WIDTH;

public class UIPost {

    private static final String UPVOTE_SYMBOL = "▲ ";
    private static final String DOWNVOTE_SYMBOL = "▼ ";

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
            console.info("\n--- Posts in " + communityName + " ---");
        } else {
            console.info("\n--- Global Feed ---");
        }

        if (posts.isEmpty()) {
            if (communityName != null) {
                console.info("No posts in this r/. Be the first to post!");
            } else {
                console.info("No posts yet. Create the first one!");
            }
            return;
        }

        User user = AppHandler.getInstance().getCurrentUser();
        Map<Long, Integer> userVotes = VoteService.getInstance().getAllUserVotes(user);

        for (Post post : posts) {
            Integer userVote = userVotes.get(post.getId());
            showPostSimple(post, userVote);
        }
    }

    public void showPostSimple(Post post, Integer vote) {
        String formattedVotes = formatVotes(post, vote);

        String postLine = String.format("ID: %d | Title: %s | Author: %s | %s",
                post.getId(),
                post.getPostTitle(),
                post.getAuthorUsername(),
                formattedVotes);

        console.info(postLine);
    }

    public void showPostExpanded(Post post) {
        String title = post.getPostTitle();
        List<String> lines = new ArrayList<>();
        User user = AppHandler.getInstance().getCurrentUser();
        Integer vote = VoteService.getInstance().getUserVoteOnPost(post, user);

        lines.add("Author: " + post.getAuthorUsername());
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(post.getPostContents(), MAX_TEXT_WIDTH);
        lines.addAll(wrappedContent);

        lines.add("");
        lines.add(formatVotes(post, vote));

        String boxedPost = BoxPadder.format(lines, title);
        console.info(boxedPost);
    }

    private String formatVotes(Post post, Integer vote) {
        String upVoteStr = UPVOTE_SYMBOL + post.getUpVotes();
        String downVoteStr = DOWNVOTE_SYMBOL + post.getDownVotes();

        if (vote != null) {
            if (vote == 1) {
                upVoteStr = Color.textGreen(upVoteStr);
            } else if (vote == -1) {
                downVoteStr = Color.textRed(downVoteStr);
            }
        }

        return upVoteStr + " | " + downVoteStr;
    }

}