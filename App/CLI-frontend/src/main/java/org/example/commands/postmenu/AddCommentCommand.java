package org.example.commands.postmenu;

import org.example.Comment;
import org.example.Community;
import org.example.Post;
import org.example.User;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.loggers.Logger;
import org.example.userinterface.readers.Console;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

public class AddCommentCommand implements Command {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    AppHandler appHandler = AppHandler.getInstance();
    Console console = Console.getInstance();



    @Override
    public boolean execute(){

        User authorUser = appHandler.getCurrentUser();
        Post targetPost = appHandler.getCurrentPost();

        try{
            String text = console.getMultiLineInput("Write Comment");
            Map<String, Object> createDto = new HashMap<>();
            createDto.put("text",text);
            createDto.put("postId",targetPost.getId());
            if(!(appHandler.getCurrentComment() ==null)) {
                createDto.put("parentCommentId", appHandler.getCurrentComment().getIdParent());
            }
            Comment createdComment = restClient.post()
                    .uri("/api/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (appHandler.getJwtToken() != null) {
                            headers.setBearerAuth(appHandler.getJwtToken());
                        }
                    })
                    .body(createDto)
                    .retrieve()
                    .body(Comment.class);
            appHandler.setCurrentState(AppHandler.State.ON_POST);
            Logger.info("Comment created successfully with ID: " + (createdComment != null ? createdComment.getId() : "null"));
            console.success("Comment added successfully!");

        }catch (HttpClientErrorException e) {
        System.out.println("STATUS: " + e.getStatusCode());
        System.out.println("RESPONSE: " + e.getResponseBodyAsString());
    }
        return true;
    }
}
