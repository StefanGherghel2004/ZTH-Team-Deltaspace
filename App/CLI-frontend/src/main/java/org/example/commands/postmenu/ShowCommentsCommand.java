package org.example.commands.postmenu;

import org.example.Comment;
import org.example.Community;
import org.example.Post;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.loggers.Logger;
import org.example.userinterface.readers.Console;
import org.example.userinterface.views.UIComment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;


import java.util.*;

public class ShowCommentsCommand implements Command {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();
    AppHandler appHandler = AppHandler.getInstance();
    Console console = Console.getInstance();
    @Override
    public boolean execute() {
        Post currentPost = appHandler.getCurrentPost();
        if(currentPost==null) {
            return true;
        }

        List<Comment> comments = fetchCommentsById(currentPost.getId());
        Map<UUID ,List<Comment>> commentTree = buildCommentTree(comments);
        UIComment.getInstance().showCommentTree(commentTree);


        console.getStringInput("Press Enter to return to the post menu...", true);
        return true;
    }
    private Map<UUID, List<Comment>> buildCommentTree(List<Comment> comments) {
        Map<UUID, List<Comment>> commentTree = new HashMap<>();

        if (comments != null) {
            for (Comment comment : comments) {
                UUID parentId = comment.getIdParent() !=null ? comment.getIdParent(): new UUID(0L,0L) ;
                commentTree.computeIfAbsent(parentId, k -> new ArrayList<>()).add(comment);
            }
        }

        return commentTree;
    }

    private List<Comment> fetchCommentsById(UUID postId){
        try{return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/comments")
                        .queryParam("postId", postId)
                        .build())
                .headers(headers -> {
                    if (appHandler.getJwtToken() != null) {
                        headers.setBearerAuth(appHandler.getJwtToken());
                    }
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<Comment>>() {});
        }catch(Exception e ){
                Logger.severe("Failed to fetch comments for post ID " + postId + ": " + e.getMessage());
                console.error("Failed to fetch comments" + e.getMessage());
                return Collections.emptyList();
                }
    }

}