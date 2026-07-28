package org.example.commands.mainmenu;


import org.example.Post;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.response.ApiResponse;
import org.example.userinterface.readers.Console;
import org.example.userinterface.views.UIPost;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShowFeedCommand implements Command {

    private static final String BASE_URL = "http://localhost:8080/api/posts";
    private final RestClient restClient = RestClient.create();

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        UIPost uiPost = UIPost.getInstance();

        List<Post> posts = new ArrayList<>();

        try {
            ApiResponse<List<Post>> responseWrapper = restClient.get()
                    .uri(BASE_URL)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<Post>>>() {});

            if (responseWrapper != null && responseWrapper.isSuccess() && responseWrapper.getData() != null) {
                posts = responseWrapper.getData();
            }

        } catch (Exception e) {
            console.error("Failed to load feed from server: " + e.getMessage());
            return true;
        }

        uiPost.showFeed(posts);

        if (posts.isEmpty()) {
            return true;
        }

        try {

            String input = console.getStringInput("Choose a post (or press Enter to go back): ", true);

            if (!input.isEmpty()) {
                try {
                    int selectedIndex = Integer.parseInt(input);


                    if (selectedIndex < 1 || selectedIndex > posts.size()) {
                        console.error("Invalid choice!");
                        return true;
                    }

                    Post selectedPost = posts.get(selectedIndex - 1);
                    app.setCurrentPost(selectedPost);
                    app.setCurrentState(AppHandler.State.ON_POST);

                } catch (NumberFormatException e) {
                    console.error("Invalid input! Please enter a valid number.");
                }
            }
        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        }

        return true;
    }
}