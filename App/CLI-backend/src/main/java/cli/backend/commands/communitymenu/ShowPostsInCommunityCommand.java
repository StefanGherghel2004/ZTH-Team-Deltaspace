package cli.backend.commands.communitymenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.database.PostRepository;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.views.UIPost;

import java.util.List;

public class ShowPostsInCommunityCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        PostRepository postRepository = PostRepository.getInstance();
        String communityName = app.getCurrentCommunity().getNickname();
        List<Post> communityPosts = postRepository.getCommunityPosts(communityName);

        UIPost.getInstance().showFeed(communityPosts, communityName);

        if (communityPosts.isEmpty()) {
            return true;
        }

        String input = console.getStringInput("Choose a post [ID] (or press Enter to go back): ", true);

        if (input.isEmpty()) return true;

        try {
            int id = Integer.parseInt(input);
            Post currentPost = app.getCurrentCommunity().findPostById(id);

            if (currentPost != null) {
                if (currentPost.isNSFW() && !app.getCurrentUser().checkAge()) {
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