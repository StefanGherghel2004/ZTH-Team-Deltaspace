package org.example.commands;

import org.example.Community;
import org.example.Post;
import org.example.apiclients.PostApiClient;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class CreatePostCommand implements Command {

    private final PostApiClient postApiClient = PostApiClient.getInstance();
    private final CheckImage checkImage = CheckImage.getInstance();

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        console.info("Welcome to the post creation page.");
        Community targetCommunity = app.getCurrentCommunity();

        try {

            String communityName = null;
            if (targetCommunity != null) {
                communityName = targetCommunity.getNickname();
            } else {
                String input = console.getStringInput("Please enter the community in which you would" +
                        " like to post \n(or press Enter to post to u/" + app.getCurrentUser().getUsername() + "):", true);

                if (!input.isEmpty()) {
                    communityName = input.trim();
                }
            }

            String postTitle = console.getStringInput("Please enter post title:");
            String postContents = console.getMultiLineInput("Please enter post contents:");

            String imagePath = console.getStringInput("Please enter image path (or press Enter to skip):", true);
            String imageFilter = null;

            if (!imagePath.isEmpty()) {
                imageFilter = console.getStringInput("Please enter filter (or press Enter to skip):", true);
                if (imageFilter.isEmpty()) {
                    imageFilter = null;
                }
            }

            boolean isNsfw = console.getUserConfirmation("Is your post NSFW? [yes/no]: ");
            if(isNsfw){
                if(Period.between(LocalDate.parse(app.getCurrentUser().getDateOfBirth()),LocalDate.now()).getYears()<18){
                    console.error("You must be at least 18 years old to create an NSFW post.");
                }
            }

                MultiValueMap<String, Object> postData = new LinkedMultiValueMap<>();

                postData.add("title", postTitle);
                postData.add("content", postContents);
                postData.add("nsfw", isNsfw);

                if (communityName != null) {
                    postData.add("communityName", communityName);
                }
                if (imageFilter != null) {
                    postData.add("filter", imageFilter);
                }

                if (!imagePath.isEmpty()) {
                    try {
                        MultipartFile multipartFile = checkImage.convertToMultipartFile(imagePath);

                        if (multipartFile != null) {

                            ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
                                @Override
                                public String getFilename() {
                                    return multipartFile.getOriginalFilename();
                                }
                            };
                            postData.add("image", fileResource);
                        }
                    } catch (IllegalArgumentException | IOException e) {
                        console.error(e.getMessage());
                        return true;
                    }
                }

                Post newPost = postApiClient.createPost(postData, app.getJwtToken());

                if (newPost != null) {
                    console.success("Post created successfully!");
                    app.setCurrentPost(newPost);
                    app.setCurrentState(AppHandler.State.ON_POST);
                } else {
                    console.error("Failed to create post. Server returned an unknown error.");
                }

        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        } catch (HttpClientErrorException e) {
            console.error("Failed to create post: Invalid data or permission denied. (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            console.error("Network error occurred while creating the post: " + e.getMessage());
        }

        return true;
    }
}
