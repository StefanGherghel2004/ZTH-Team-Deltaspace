package cli.backend.commands.communitymenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import java.util.List;

public class ShowPostsInCommunityCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        ConsoleReader consoleReader = ConsoleReader.getInstance();

        System.out.println("\n--- Posts in " + app.getCurrentCommunity().getNickname() + " ---");
        List<Post> communityPosts = app.getCurrentCommunity().getPosts();

        if (communityPosts.isEmpty()) {
            System.out.println("No posts in this r/");
            return true;
        }

        for (Post post : communityPosts) {
            System.out.println("ID: " + post.getPostID() + " | Title: " + post.getPostTitle() + " | Author: " + post.getUser().getUsername());
        }

        System.out.print("\nChoose a post [ID] (or press Enter to go back): ");
        String input = consoleReader.readString();

        if (input.isEmpty()) return true;

        try {
            int id = Integer.parseInt(input);
            Post currentPost = app.getCurrentCommunity().findPostById(id);

            if (currentPost != null) {
                if (currentPost.getNSFW() && !app.getCurrentUser().checkAge()) {
                    System.out.println("This post is marked as NSFW. You must be at least 18 years old to view it.");
                } else {
                    app.setCurrentPost(currentPost);
                    app.setCurrentState(AppHandler.State.ON_POST);
                }
            } else {
                System.out.println("Post not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
        return true;
    }
}