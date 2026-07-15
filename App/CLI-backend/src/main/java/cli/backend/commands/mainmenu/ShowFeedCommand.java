package cli.backend.commands.mainmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.PostService;
import java.util.List;

public class ShowFeedCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        PostService postService = PostService.getInstance();
        Console console = Console.getInstance();

        console.info("\n--- Your Feed ---");
        List<Post> posts = postService.getPosts();

        if (posts.isEmpty()) {
            console.getStringInput("Feed is empty.\nPress Enter to return...", true);
            return true;
        }

        for(Post post: posts) {
            console.info("ID: " + post.getPostID() + " | Title: " + post.getPostTitle() + " | Community: " + post.getCommunityName());
        }

        String input = console.getStringInput("Choose a post [ID] (or press Enter to go back): ", true);

        if (!input.isEmpty()) {
            try {
                int id = Integer.parseInt(input);
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