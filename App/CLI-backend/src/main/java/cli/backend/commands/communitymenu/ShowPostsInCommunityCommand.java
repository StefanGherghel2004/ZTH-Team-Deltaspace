package cli.backend.commands.communitymenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.ConsoleLogger;
import cli.backend.loggers.LogLevel;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import java.util.List;

public class ShowPostsInCommunityCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        console.info("\n--- Posts in " + app.getCurrentCommunity().getNickname() + " ---");
        List<Post> communityPosts = app.getCurrentCommunity().getPosts();

        if (communityPosts.isEmpty()) {
            console.info("No posts in this r/");
            return true;
        }

        for (Post post : communityPosts) {
            console.info("ID: " + post.getPostID() + " | Title: " + post.getPostTitle() + " | Author: " + post.getUser().getUsername());
        }

        // this will be changed when extracting more common behavior in the console class
        String input = console.getStringInput("Choose a post [ID] (or press Enter to go back): ");

        if (input.isEmpty()) return true;

        try {
            int id = Integer.parseInt(input);
            Post currentPost = app.getCurrentCommunity().findPostById(id);

            if (currentPost != null) {
                if (currentPost.getNSFW() && !app.getCurrentUser().checkAge()) {
                    console.error("This post is marked as NSFW. You must be at least 18 years old to view it.");
                } else {
                    app.setCurrentPost(currentPost);
                    app.setCurrentState(AppHandler.State.ON_POST);
                }
            } else {
                console.error("Post not found!");
            }
        } catch (NumberFormatException e) {
            console.error("Invalid ID format!");
        }
        return true;
    }
}