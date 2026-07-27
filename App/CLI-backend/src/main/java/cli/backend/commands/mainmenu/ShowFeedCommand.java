package cli.backend.commands.mainmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.PostService;
import cli.backend.userinterface.views.UIPost;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowFeedCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        PostService postService = PostService.getInstance();
        Console console = Console.getInstance();

        List<Post> posts = postService.getFeedFromRepository();
        UIPost uiPost = UIPost.getInstance();
        List<Post> shuffledPosts = new ArrayList<>(posts);
        Collections.shuffle(shuffledPosts);

        uiPost.showFeed(shuffledPosts);

        if (posts.isEmpty()) {
            return true;
        }

        String input = console.getStringInput("Choose a post [ID] (or press Enter to go back): ", true);

        if (!input.isEmpty()) {
            try {
                Long id = Long.parseLong(input);
                Post foundPost = postService.findPostById(id);
                if (foundPost != null) {
                    if (foundPost.isNSFW() && !app.getCurrentUser().checkAge()) {
                        console.error("This post is marked as NSFW. You must be at least 18 years old to view it.");
                    } else {
                        app.setCurrentPost(foundPost);
                        app.setCurrentState(AppHandler.State.ON_POST);
                    }
                } else {
                    console.error("Post not found!");
                }
            } catch (NumberFormatException e) {
                console.error("Invalid ID format!");
            }
        }
        return true;
    }
}