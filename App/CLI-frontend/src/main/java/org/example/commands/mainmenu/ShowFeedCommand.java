package org.example.commands.mainmenu;


import org.example.Post;
import org.example.apiclients.PostApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.example.userinterface.views.UIPost;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ShowFeedCommand implements Command {

    private final PostApiClient postApiClient = PostApiClient.getInstance();

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        UIPost uiPost = UIPost.getInstance();

        List<Post> posts = postApiClient.getPosts(null, app.getJwtToken());

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
                    if(selectedPost!=null){
                        if(selectedPost.isNSFW()){
                            if(Period.between(LocalDate.parse(app.getCurrentUser().getDateOfBirth()),LocalDate.now()).getYears()<18){
                                console.error("This post is marked as NSFW. You must be at least 18 years old to view it.");
                                return true;
                            }
                        }
                    }
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