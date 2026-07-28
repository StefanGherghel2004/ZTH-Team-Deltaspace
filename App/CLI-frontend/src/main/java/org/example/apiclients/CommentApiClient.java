package org.example.apiclients;

import org.example.Comment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    .uri("/api/comments")
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
                    .uri("/api/comments/{id}", id)
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
                        uriBuilder.path("/api/comments");
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
                    .uri("/api/comments/{id}", id)
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
                    .uri("/api/comments/{id}", id)
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
}