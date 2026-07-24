package cli.backend.commands;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.Logger;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommunityService;
import cli.backend.services.PostService;


public class CreatePostCommand implements Command {
    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        PostService postService = PostService.getInstance();
        CheckImage checkImage = CheckImage.getInstance();

        console.info("Welcome to the post creation page.");
        Community targetCommunity = app.getCurrentCommunity();

        if (targetCommunity == null) {
            String input = console.getStringInput("Please enter the community in which you would" +
                " like to post \n(or press Enter to post to u/" + app.getCurrentUser().getUsername() + "):", true);

            if (input.isEmpty()) {
                console.info("Posting to your profile (u/" + app.getCurrentUser().getUsername() + ").");
            } else {
                String communityName = communityService.formatName(input);
                targetCommunity = communityService.getCommunityByName(communityName);
                if (targetCommunity == null) {
                    console.error("Community not found! Posting to your profile instead.");
                }
            }
        }

        String postTitle = console.getStringInput("Please enter post title:");
        String postContents = console.getMultiLineInput("Please enter post contents:");

        String imagePath= console.getStringInput("Please enter image path (or press Enter to skip):", true);
        if (imagePath.isEmpty()) {
            imagePath = null;
        }

        boolean NSFW = console.getUserConfirmation("Is your post NSFW? [yes/no]");

        if (NSFW && !app.getCurrentUser().checkAge()) {
            console.error("You must be at least 18 years old to create an NSFW post.");
        } else {
            try{
                String savedImagePath=checkImage.processAndSaveImage(imagePath);
                    Post newPost = postService.addPost(app.getCurrentUser().getUsername(), postTitle, postContents,
                            savedImagePath, NSFW, targetCommunity,0,0);
                    console.success("Post created successfully!");
                    app.setCurrentPost(newPost);
                    app.setCurrentState(AppHandler.State.ON_POST);
                }catch (IllegalArgumentException | java.io.IOException e) {
                console.error(e.getMessage());
                Logger.severe("Post could not be created :" + e.getMessage());
            }
        }

        return true;

    }
}