package com.example.demo.service;

import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.logger.Logger;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostSummaryService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private RestClient restClient;

    @PostConstruct
    private void init() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public User getOrCreateTldrBotUser() {
        return userRepository.findByUsername("tldr-bot")
                .orElseGet(() -> {
                    UserCreateDto userCreateDto =
                            new UserCreateDto("tldr-bot", "tldr@bot.com",
                                    "tldrbotpassword!", null);
                    return userService.addUser(userCreateDto);
                });
    }

    public String generateTldr(String title, String content) {
        String prompt = """
        You are a forum post summarizer.
        Write a concise TL;DR summary of approximately 80 to 110 words (2 to 4 sentences) for the following long post.
        Capture the background context, the main argument or problem, and the key question or takeaway.
        Do not use conversational filler, greetings, or intro/outro meta-text (e.g., do NOT start with "TL;DR:" or "Here is a summary:").
        
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
            Logger.severe(e.getMessage());
            System.err.println("Failed to generate TL;DR from Groq: " + e.getMessage());
        }

        return null;
    }

    public Comment addTldrComment(Post post) {
        if (post == null || post.getContent() == null || post.getTitle() == null) {
            return null;
        }

        if (post.getContent().length() > 1500) {
            String tldr = generateTldr(post.getTitle(), post.getContent());
            if (tldr == null) {
                return null;
            }

            Comment tldrComment = new Comment();
            User tldrBotUser = getOrCreateTldrBotUser();
            tldrComment.setParentComment(null);
            tldrComment.setPost(post);
            tldrComment.setUser(tldrBotUser);
            tldrComment.setContent("TL;DR " + tldr);
            commentRepository.save(tldrComment);
            Logger.info("Successfully added TL;DR comment.");
            return tldrComment;
        }
        return null;
    }

    public Comment updateTldrComment(Post post) {
        if (post == null || post.getContent() == null || post.getTitle() == null) {
            return null;
        }

        User tldrBot = getOrCreateTldrBotUser();
        Comment tldrComment = commentRepository.findByPostIdAndUserId(post.getId(), tldrBot.getId());

        if (tldrComment == null) {
            return addTldrComment(post);
        }

        if (post.getContent().length() <= 1500) {
            commentRepository.delete(tldrComment);
            return null;
        }

        String tldr = generateTldr(post.getTitle(), post.getContent());
        if (tldr != null) {
            tldrComment.setContent("TL;DR " + tldr);
            return commentRepository.save(tldrComment);
        }

        return tldrComment;
    }

    private record GroqResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}