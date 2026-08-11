package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class PostSummaryService {
    private final RestClient restClient;

    public PostSummaryService(@Value("${groq.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String generateTldr(String title, String content) {
        String prompt = """
                You are a forum post summarizer.
                Create a single-sentence TL;DR summary for the following post.
                Keep it under 25 words and directly state the core question or topic.
                Do not include intro/outro fluff (e.g. do NOT say "Here is a summary:").
                
                Title: %s
                Content: %s
                """.formatted(title, content);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            GroqResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GroqResponse.class);

            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                return response.choices().get(0).message().content().trim();
            }
        } catch (Exception e) {
            System.err.println("Failed to generate TL;DR from Groq: " + e.getMessage());
        }

        return null;
    }

    private record GroqResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}
