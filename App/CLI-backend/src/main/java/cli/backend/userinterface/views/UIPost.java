package cli.backend.userinterface.views;


import cli.backend.Post;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;

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

    public void showFeed(List<Post> posts) {
        if (posts.isEmpty()) {
            console.info("No posts yet. Create the first one!");
            return;
        }

        for (Post post : posts) {
            showPostSimple(post);
        }
    }

    public void showPostSimple(Post post) {
        String postLine = String.format("ID: %d | Title: %s | Author: %s | Up %d | Down %d",
                post.getId(),
                post.getPostTitle(),
                post.getAuthorUsername(),
                post.getUpVotes(),
                post.getDownVotes());

        console.info(postLine);
    }

    public void showPostExpanded(Post post) {
        String title = post.getPostTitle();
        List<String> lines = new ArrayList<>();

        lines.add("Author: " + post.getAuthorUsername());
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(post.getPostContents(), MAX_TEXT_WIDTH);
        lines.addAll(wrappedContent);

        lines.add("Up " + post.getUpVotes() + " | Down " + post.getDownVotes());

        String boxedPost = BoxPadder.format(lines, title);
        console.info(boxedPost);
    }

}