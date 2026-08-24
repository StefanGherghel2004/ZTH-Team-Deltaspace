package com.example.demo.service;

import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.logger.Logger;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostSummaryService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    private final DiffMatchPatch dmp = new DiffMatchPatch();
    private static final double SIGNIFICANT_CHANGE_THRESHOLD = 0.20;
    private static final int MIN_CHAR_CHANGE = 300;
    private static final int MAX_COMMENT_LENGTH = 1000;

    private RestClient restClient;

    @PostConstruct
    private void init() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Async
    @Transactional
    public void addTldrCommentAsync(UUID postId) {
        postRepository.findById(postId).ifPresent(this::addTldrComment);
    }

    @Async
    @Transactional
    public void updateTldrCommentAsync(UUID postId, String oldContents) {
        postRepository.findById(postId).ifPresent(post -> updateTldrComment(post, oldContents));
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
        int charCount = content.length();
        String lengthInstruction;

        if (charCount <= 2500) {
            lengthInstruction = "approximately 35 to 50 words (strictly 2 sentences)";
        } else if (charCount <= 5000) {
            lengthInstruction = "approximately 60 to 80 words (2 to 3 sentences)";
        } else if (charCount <= 7500) {
            lengthInstruction = "approximately 85 to 105 words (3 to 4 sentences)";
        } else {
            lengthInstruction = "strictly between 110 and 130 words (4 to 5 sentences)";
        }

        String prompt = """
        You are a forum post summarizer.
        Write a concise TL;DR summary in a single continuous paragraph of %s for the following post.
        Capture the background context, the main argument or problem, and the key question or takeaway.
        Do not use bullet points, lists, line breaks, conversational filler, greetings, or intro/outro meta-text (e.g., do NOT start with "TL;DR:" or "Here is a summary:").
        
        Title: %s
        Content: %s
        """.formatted(lengthInstruction, title, content);

        Map<String, Object> requestBody = Map.of(
                "model", model,
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
            Logger.severe("Failed to generate TL;DR from Groq: " + e.getMessage());
        }

        return null;
    }

    public Comment addTldrComment(Post post) {
        if (post == null || post.getContent() == null || post.getTitle() == null) {
            return null;
        }

        if (post.getContent().length() > 1500) {
            String tldr = generateTldr(post.getTitle(), post.getContent());
            if (tldr == null || tldr.trim().isBlank()) {
                return null;
            }

            String finalContent = "TL;DR " + tldr.trim();
            if (finalContent.length() > MAX_COMMENT_LENGTH) {
                finalContent = finalContent.substring(0, MAX_COMMENT_LENGTH - 3) + "...";
            }

            Comment tldrComment = new Comment();
            User tldrBotUser = getOrCreateTldrBotUser();
            tldrComment.setParentComment(null);
            tldrComment.setPost(post);
            tldrComment.setUser(tldrBotUser);
            tldrComment.setContent(finalContent);
            commentRepository.save(tldrComment);
            Logger.info("Successfully added TL;DR comment.");
            return tldrComment;
        }
        return null;
    }

    public Comment updateTldrComment(Post post, String oldContents) {
        if (post == null || post.getContent() == null || post.getTitle() == null) {
            return null;
        }

        User tldrBot = getOrCreateTldrBotUser();
        Comment tldrComment = commentRepository.findByPostIdAndUserId(post.getId(), tldrBot.getId());

        if (post.getContent().length() <= 1500) {
            if (tldrComment != null) {
                if (post.getComments() != null) {
                    post.getComments().remove(tldrComment);
                }
                commentRepository.delete(tldrComment);
                commentRepository.flush();
            }
            return null;
        }

        if (tldrComment == null) {
            return addTldrComment(post);
        }

        if (!isSignificantChange(oldContents, post.getContent())) {
            return tldrComment;
        }

        String tldr = generateTldr(post.getTitle(), post.getContent());
        if (tldr != null && !tldr.trim().isBlank()) {
            String finalContent = "TL;DR " + tldr.trim();
            if (finalContent.length() > MAX_COMMENT_LENGTH) {
                finalContent = finalContent.substring(0, MAX_COMMENT_LENGTH - 3) + "...";
            }
            tldrComment.setContent(finalContent);
            Logger.info("Successfully updated TL;DR comment for post %s.", post.getId());
            return commentRepository.save(tldrComment);
        }

        return tldrComment;
    }

    public boolean isSignificantChange(String oldContent, String newContent) {
        if (Objects.equals(oldContent, newContent)) {
            return false;
        }

        if (oldContent == null || newContent == null) {
            return true;
        }

        if (oldContent.trim().equals(newContent.trim())) {
            return false;
        }

        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(oldContent, newContent);
        dmp.diffCleanupSemantic(diffs);

        int totalDeletedChars = 0;
        int totalInsertedChars = 0;

        for (DiffMatchPatch.Diff diff : diffs) {
            if (diff.operation == DiffMatchPatch.Operation.DELETE) {
                totalDeletedChars += diff.text.length();
            } else if (diff.operation == DiffMatchPatch.Operation.INSERT) {
                totalInsertedChars += diff.text.length();
            }
        }

        int changedChars = totalDeletedChars + totalInsertedChars;
        int baseLength = Math.max(oldContent.length(), newContent.length());

        if (baseLength == 0) {
            return false;
        }

        double changeRatio = (double) changedChars / baseLength;

        return changedChars >= MIN_CHAR_CHANGE || changeRatio >= SIGNIFICANT_CHANGE_THRESHOLD;
    }

    private record GroqResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}