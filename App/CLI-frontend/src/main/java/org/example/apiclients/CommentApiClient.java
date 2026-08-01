package org.example.apiclients;

import org.example.Comment;
import org.example.response.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;

public class CommentApiClient {

    private static CommentApiClient instance;
    private final RestClient restClient;
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private CommentApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }

    public static synchronized CommentApiClient getInstance() {
        if (instance == null) {
            instance = new CommentApiClient();
        }
        return instance;
    }

    private String cleanBearerToken(String token) {
        if (token == null || token.isBlank() || token.equalsIgnoreCase("null")) {
            return "";
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    public Comment addComment(Map<String, Object> commentPayload, String token) {
        try {
            return restClient.post()
                    .uri("/comments")
                    .header("Authorization", "Bearer " + cleanBearerToken(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(commentPayload)
                    .retrieve()
                    .body(Comment.class);
        } catch (HttpClientErrorException.BadRequest e) {
            System.err.println("Failed to create comment (400 Bad Request): " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Unauthorized: Invalid or expired token.");
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
        }
        return null;
    }

    public Comment getCommentById(UUID id, String token) {
        try {
            return restClient.get()
                    .uri("/comments/{id}", id)
                    .header("Authorization", "Bearer " + cleanBearerToken(token))
                    .retrieve()
                    .body(Comment.class);
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("Comment not found with ID: " + id);
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Unauthorized: Invalid or expired token.");
        } catch (Exception e) {
            System.err.println("Error fetching comment: " + e.getMessage());
        }
        return null;
    }

    public List<Comment> getComments(UUID postId, String token) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/comments");
                        if (postId != null) {
                            uriBuilder.queryParam("postId", postId);
                        }
                        return uriBuilder.build();
                    })
                    .header("Authorization", "Bearer " + cleanBearerToken(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Comment>>() {});
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Unauthorized: Invalid or expired token.");
        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Comment updateComment(UUID id, Map<String, Object> updatePayload, String token) {
        try {
            return restClient.put()
                    .uri("/comments/{id}", id)
                    .header("Authorization", "Bearer " + cleanBearerToken(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updatePayload)
                    .retrieve()
                    .body(Comment.class);
        } catch (HttpClientErrorException.BadRequest e) {
            System.err.println("Failed to update comment (400 Bad Request): " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("Comment not found with ID: " + id);
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Unauthorized: Invalid or expired token.");
        } catch (Exception e) {
            System.err.println("Error updating comment: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteCommentById(UUID id, String token) {
        try {
            restClient.delete()
                    .uri("/comments/{id}", id)
                    .header("Authorization", "Bearer " + cleanBearerToken(token))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("Comment not found with ID: " + id);
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("Forbidden: You can only delete your own comments.");
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Unauthorized: Invalid or expired token.");
        } catch (Exception e) {
            System.err.println("Error deleting comment: " + e.getMessage());
        }
        return false;
    }

    public boolean voteComment(Comment comment, String voteType, String token){
        try{
            Map<String,String> body = new HashMap<>();
            body.put("voteType",voteType);

            ApiResponse<Map<String, Object>> response = restClient.put()
                    .uri("/comments/{id}/vote", comment.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {});

            if (response != null && response.isSuccess() && response.getData() != null) {
                Map<String, Object> data = response.getData();
                comment.setUpvotes((Integer) data.get("upvotes"));
                comment.setDownvotes((Integer) data.get("downvotes"));
                comment.setUserVote((String) data.get("userVote"));
                return true;
            }
        } catch (Exception e) {
        System.err.println("Error sending vote: " + e.getMessage());
        }
        return false;
    }
}