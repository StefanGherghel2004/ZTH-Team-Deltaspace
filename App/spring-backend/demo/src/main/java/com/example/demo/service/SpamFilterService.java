package com.example.demo.service;

import com.example.demo.logger.Logger;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SpamFilterService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    private RestClient restClient;

    private static final int MAX_LINKS = 2;
    private static final int MAX_MENTIONS = 5;
    private static final int MAX_HASHTAGS = 5;
    private static final double MAX_CAPS_RATIO = 0.7;
    private static final int MIN_LEN_FOR_CAPS_CHECK = 10;

    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+|www\\.\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\w+");
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");
    private static final Pattern REPEATED_CHAR_PATTERN = Pattern.compile("(.)\\1{5,}");

    private final List<Pattern> spamPhrasePatterns = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/spam-phrases.txt")),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    spamPhrasePatterns.add(Pattern.compile(Pattern.quote(line), Pattern.CASE_INSENSITIVE));
                }
            }
        } catch (Exception e) {
            Logger.severe("Failed to load spam phrases, using default fallback phrases: " + e.getMessage());
            List.of("buy now", "click here", "act now", "limited time offer", "make money fast")
                    .forEach(p -> spamPhrasePatterns.add(Pattern.compile(Pattern.quote(p), Pattern.CASE_INSENSITIVE)));
        }
    }

    public boolean isSpam(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        // 1. Fast local heuristic checks
        if (isLocalSpam(text)) {
            return true;
        }

        // 2. Groq LLM check for borderline evasive spam
        if (looksBorderline(text)) {
            try {
                return checkGroq(text);
            } catch (Exception e) {
                Logger.severe("Failed to classify spam using Groq: " + e.getMessage());
            }
        }

        return false;
    }

    private boolean isLocalSpam(String text) {
        if (countMatches(URL_PATTERN, text) > MAX_LINKS) return true;
        if (countMatches(MENTION_PATTERN, text) > MAX_MENTIONS) return true;
        if (countMatches(HASHTAG_PATTERN, text) > MAX_HASHTAGS) return true;
        if (REPEATED_CHAR_PATTERN.matcher(text).find()) return true;
        if (text.length() >= MIN_LEN_FOR_CAPS_CHECK && capsRatio(text) > MAX_CAPS_RATIO) return true;

        String lower = text.toLowerCase();
        for (Pattern p : spamPhrasePatterns) {
            if (p.matcher(lower).find()) return true;
        }

        return false;
    }

    private boolean looksBorderline(String text) {
        String lower = text.toLowerCase();
        return text.length() > 30 && (lower.contains("http") ||
                lower.matches(".*\\b(discount|offer|deal|free|win|prize|promo|bonus|claim)\\b.*"));
    }

    private boolean checkGroq(String text) {
        String prompt = """
            You are a strict content moderation filter.
            Analyze the following text and determine if it is SPAM.
            Spam includes marketing promos, scams, phishing links, self-promotion, fake giveaways, or engagement bait.

            Respond with ONLY the word "YES" if it is spam, or "NO" if it is not.

            Text: %s
            """.formatted(text);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", 0.0,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        GroqResponse response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(GroqResponse.class);

        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            String answer = response.choices().get(0).message().content().trim();
            return answer.equalsIgnoreCase("YES");
        }

        return false;
    }

    private int countMatches(Pattern p, String text) {
        var m = p.matcher(text);
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    private double capsRatio(String text) {
        long letters = text.chars().filter(Character::isLetter).count();
        if (letters == 0) return 0.0;
        long upper = text.chars().filter(Character::isUpperCase).count();
        return (double) upper / letters;
    }

    private record GroqResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}