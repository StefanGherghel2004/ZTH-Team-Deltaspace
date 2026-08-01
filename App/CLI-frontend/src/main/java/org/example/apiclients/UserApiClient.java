package org.example.apiclients;

import org.example.User;
import org.example.handlers.AppHandler;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private static UserApiClient instance;
    private final RestClient restClient;

    public record AuthResponse(String token, User user) {}

    private UserApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }

    public static UserApiClient getInstance() {
        if (instance == null) {
            instance = new UserApiClient();
        }
        return instance;
    }

    public AuthResponse login(String usernameOrEmail, String password) throws HttpClientErrorException.Unauthorized {
        return restClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("usernameOrEmail", usernameOrEmail, "password", password))
                .retrieve()
                .body(AuthResponse.class);
    }

    public User registerUser(Map<String, Object> userCreatePayload) throws HttpClientErrorException.Conflict {
        return restClient.post()
                .uri("/users/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userCreatePayload)
                .retrieve()
                .body(User.class);
    }

    public User updateUser(String username, Map<String, Object> updateFields, String token) {
        if (token == null || token.isBlank() || token.equalsIgnoreCase("null")) {
            System.err.println("Update failed: No active JWT session. Please log in first.");
            return null;
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        updateFields.forEach((key, value) -> {
            if (value != null) {
                body.add(key, value.toString());
            }
        });

        try {
            return restClient.put()
                    .uri("/users/{username}", username)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(User.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Update failed: Invalid or expired token.");
            return null;
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("Update failed: You don't have permission to perform this action.");
            return null;
        }
    }

    public boolean deleteUser(String username, String token) {
        if (token == null || token.isBlank() || token.equalsIgnoreCase("null")) {
            System.err.println("Delete failed: No active JWT session. Please log in first.");
            return false;
        }

        try {
            restClient.delete()
                    .uri("/users/{username}", username)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Delete failed: Invalid or expired token.");
            return false;
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("Delete failed: You don't have permission to perform this action.");
            return false;
        }
    }
}