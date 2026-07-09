package cli.backend.commands;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;
import cli.backend.services.PostService;

public class CreatePostCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        ConsoleReader consoleReader = ConsoleReader.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        PostService postService = PostService.getInstance();

        System.out.println("Welcome to the post creation page.");
        Community targetCommunity = app.getCurrentCommunity();

        if (targetCommunity == null) {
            System.out.print("Please enter the community in which you would like to post \n(or press Enter to post to u/" + app.getCurrentUser().getUsername() + "): r/");
            String input = consoleReader.readString();

            if (input.isEmpty()) {
                System.out.println("Posting to your profile (u/" + app.getCurrentUser().getUsername() + ").");
            } else {
                String communityName = "r/" + input;
                targetCommunity = communityService.getCommunityByName(communityName);
                if (targetCommunity == null) {
                    System.out.println("Community not found! Posting to your profile instead.");
                }
            }
        }

        System.out.println("Please enter post title:");
        String postTitle = consoleReader.readString();

        System.out.println("Please enter post contents:");
        String postContents = consoleReader.readString();

        System.out.println("Please enter image link (or press Enter to skip):");
        String imageLink = consoleReader.readString();
        if (imageLink.trim().isEmpty()) {
            imageLink = null;
        }

        System.out.println("Is your post NSFW? [yes/no]");
        boolean NSFW = consoleReader.readString().equalsIgnoreCase("yes");

        if (NSFW && !app.getCurrentUser().checkAge()) {
            System.out.println("You must be at least 18 years old to create an NSFW post.");
        } else {
            Post newPost = postService.addPost(app.getCurrentUser(), postTitle, postContents, imageLink, NSFW, targetCommunity);
            System.out.println("Post created successfully!");
            app.setCurrentPost(newPost);
            app.setCurrentState(AppHandler.State.ON_POST);
        }
        return true;
    }
}