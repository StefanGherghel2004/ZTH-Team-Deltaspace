package cli.backend.commands.mainmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.PostService;
import java.util.List;

public class ShowFeedCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        PostService postService = PostService.getInstance();
        ConsoleReader reader = ConsoleReader.getInstance();

        System.out.println("\n--- Your Feed ---");
        List<Post> posts = postService.getPosts();

        if (posts.isEmpty()) {
            System.out.print("Feed is empty.\nPress Enter to return...");
            reader.readString();
            return true;
        }

        for(Post post: posts) {
            System.out.println("ID: " + post.getPostID() + " | Title: " + post.getPostTitle() + " | Community: " + post.getCommunityName());
        }

        System.out.print("Choose a post [ID] (or press Enter to go back): ");
        String input = reader.readString();

        if (!input.isEmpty()) {
            try {
                int id = Integer.parseInt(input);
                Post foundPost = postService.findPostById(id);
                if (foundPost != null) {
                    if (foundPost.getNSFW() && !app.getCurrentUser().checkAge()) {
                        System.out.println("NSFW Post. You must be 18+.");
                    } else {
                        app.setCurrentPost(foundPost);
                        app.setCurrentState(AppHandler.State.ON_POST);
                    }
                } else {
                    System.out.println("Post not found!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format!");
            }
        }
        return true;
    }
}