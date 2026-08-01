package org.example.apiclients;

import org.example.Post;
import org.example.response.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;

public class PostApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static PostApiClient instance;
    private final RestClient restClient;

    private PostApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }

    public static PostApiClient getInstance() {
        if (instance == null) {
            instance = new PostApiClient();
        }
        return instance;
    }


    public Optional<Post> getPostById(UUID id, String token) {
        try {
            ApiResponse<Post> response = restClient.get()
                    .uri("/posts/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Post>>() {
                    });

            if (response != null && response.isSuccess()) {
                return Optional.ofNullable(response.getData());
            }
            return Optional.empty();
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<Post> getPosts(String subreddit, String token) {
        String uri = (subreddit != null && !subreddit.trim().isEmpty())
                ? "/posts?subreddit=" + subreddit
                : "/posts";

        try {
            ApiResponse<List<Post>> response = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<Post>>>() {
                    });

            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println(": " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public Post createPost(MultiValueMap<String, Object> postData, String token) throws HttpClientErrorException {
        ApiResponse<Post> response = restClient.post()
                .uri("/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(postData)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Post>>() {
                });

        return response != null ? response.getData() : null;
    }


    public Post updatePost(UUID id, Map<String, Object> updateFields, String token) throws HttpClientErrorException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        updateFields.forEach((key, value) -> {
            if (value != null) {
                body.add(key, value.toString());
            }
        });

        ApiResponse<Post> response = restClient.put()
                .uri("/posts/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Post>>() {
                });

        return response != null ? response.getData() : null;
    }

    public boolean deletePost(UUID id, String token) {
        try {
            ApiResponse<Void> response = restClient.delete()
                    .uri("/posts/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Void>>() {
                    });

            return response != null && response.isSuccess();
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            System.err.println("Delete failed: You do not have permission to delete this post.");
            return false;
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("Delete failed: Post not found.");
            return false;
        } catch (Exception e) {
            System.err.println("Delete failed due to network error: " + e.getMessage());
            return false;
        }
    }

    public boolean votePost(Post post, String voteType, String token) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("voteType", voteType);

            ApiResponse<Map<String, Object>> response = restClient.put()
                    .uri("/posts/{id}/vote", post.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {});

            if (response != null && response.isSuccess() && response.getData() != null) {
                Map<String, Object> data = response.getData();
                post.setUpvotes((Integer) data.get("upvotes"));
                post.setDownvotes((Integer) data.get("downvotes"));
                post.setUserVote((String) data.get("userVote"));
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error sending vote: " + e.getMessage());
        }
        return false;
    }
}