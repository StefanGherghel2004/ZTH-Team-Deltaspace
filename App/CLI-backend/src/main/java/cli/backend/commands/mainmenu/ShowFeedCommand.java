package cli.backend.commands.mainmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.PostService;
import cli.backend.userinterface.views.UIPost;


import java.util.List;

public class ShowFeedCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        PostService postService = PostService.getInstance();
        Console console = Console.getInstance();

        List<Post> posts = postService.getFeedFromRepository();
        UIPost uiPost = UIPost.getInstance();

        uiPost.showFeed(posts, app.getCurrentUser());

        if (posts.isEmpty()) {
            console.getStringInput("Press Enter to return...", true);
            return true;
        }

        String input = console.getStringInput("Choose a post [ID] (or press Enter to go back): ", true);

        if (!input.isEmpty()) {
            try {
                Long id = Long.parseLong(input);
                Post foundPost = postService.findPostById(id);
                if (foundPost != null) {
                    if (foundPost.isNSFW() && !app.getCurrentUser().checkAge()) {
                        console.error("NSFW Post. You must be 18+.");
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