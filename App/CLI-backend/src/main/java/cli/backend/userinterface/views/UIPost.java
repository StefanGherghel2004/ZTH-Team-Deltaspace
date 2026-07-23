package cli.backend.userinterface.views;


import cli.backend.Post;
import cli.backend.User;
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

    public void showFeed(List<Post> posts, User user) {
        if (posts.isEmpty()) {
            console.info("No posts yet. Create the first one!");
            return;
        }

        Map<Long, Integer> userVotes = VoteService.getInstance().getAllUserVotes(user);

        for (Post post : posts) {
            Integer userVote = userVotes.get(post.getId());

            showPostSimple(post, userVote);
        }
    }

    public void showPostSimple(Post post, Integer vote) {
        String upVoteStr = "^ " + post.getUpVotes();
        String downVoteStr = "v " + post.getDownVotes();

        if (vote != null) {
            if (vote == 1) {
                upVoteStr = Color.textGreen(upVoteStr);
            } else if (vote == -1) {
                downVoteStr = Color.textRed(downVoteStr);
            }
        }

        String postLine = String.format("ID: %d | Title: %s | Author: %s | %s | %s",
                post.getId(),
                post.getPostTitle(),
                post.getAuthorUsername(),
                upVoteStr,
                downVoteStr);

        console.info(postLine);
    }

    public void showPostExpanded(Post post) {
        String title = post.getPostTitle();
        List<String> lines = new ArrayList<>();

        lines.add("Author: " + post.getAuthorUsername());
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(post.getPostContents(), MAX_TEXT_WIDTH);
        lines.addAll(wrappedContent);

        lines.add("");
        lines.add("Up " + post.getUpVotes() + " | Down " + post.getDownVotes());

        String boxedPost = BoxPadder.format(lines, title);
        console.info(boxedPost);
    }

}