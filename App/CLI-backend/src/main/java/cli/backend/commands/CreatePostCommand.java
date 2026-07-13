package cli.backend.commands;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.database.ExcelWrite;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommunityService;
import cli.backend.services.PostService;

import java.util.List;

public class CreatePostCommand implements Command {
    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        CommunityService communityService = CommunityService.getInstance();
        PostService postService = PostService.getInstance();
        ExcelWrite excelWrite = ExcelWrite.getInstance();

        console.info("Welcome to the post creation page.");
        Community targetCommunity = app.getCurrentCommunity();

        if (targetCommunity == null) {
            String input = console.getStringInput("Please enter the community in which you would" +
                    "like to post \n(or press Enter to post to u/" + app.getCurrentUser().getUsername() + "): r/", true);

            if (input.isEmpty()) {
                console.info("Posting to your profile (u/" + app.getCurrentUser().getUsername() + ").");
            } else {
                String communityName = "r/" + input;
                targetCommunity = communityService.getCommunityByName(communityName);
                if (targetCommunity == null) {
                    console.error("Community not found! Posting to your profile instead.");
                }
            }
        }

        String postTitle = console.getStringInput("Please enter post title:");
        String postContents = console.getStringInput("Please enter post contents:");

        String imageLink = console.getStringInput("Please enter image link (or press Enter to skip):", true);
        if (imageLink.isEmpty()) {
            imageLink = null;
        }

        boolean NSFW = console.getUserConfirmation("Is your post NSFW? [yes/no]");

        if (NSFW && !app.getCurrentUser().checkAge()) {
            console.error("You must be at least 18 years old to create an NSFW post.");
        } else {
            Post newPost = postService.addPost(app.getCurrentUser(), postTitle, postContents,
                    imageLink, NSFW, targetCommunity);

            String targetCommunityChecked = (targetCommunity != null) ? targetCommunity.getNickname() : "None";
            String imageLinkChecked = (imageLink != null) ? imageLink : "No image link";

            excelWrite.write(excelWrite.postDatabasePath, List.of(
                    String.valueOf(newPost.getPostID()),
                    app.getCurrentUser().getUsername(),
                    postTitle,postContents,
                    imageLinkChecked,
                    targetCommunityChecked,
                    String.valueOf(NSFW)));

            console.success("Post created successfully!");
            app.setCurrentPost(newPost);
            app.setCurrentState(AppHandler.State.ON_POST);
        }
        return true;
    }
}