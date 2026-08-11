package com.example.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ProfanityFilterService {

    private final TrieNode root = new TrieNode();

    private static final Map<Character, Character> LEET_MAP = Map.of(
            '@', 'a',
            '4', 'a',
            '1', 'i',
            '!', 'i',
            '0', 'o',
            '$', 's',
            '5', 's',
            '3', 'e',
            '+', 't'
    );

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("App/spring-backend/demo/profanity-words.txt")), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    addWord(line);
                }
            }
        } catch (Exception e) {
            List.of("badword", "offensive").forEach(this::addWord);
        }
    }

    public boolean containsProfanity(String text) {
        if (text == null || text.isBlank()) return false;

        String normalized = normalize(text);
        int length = normalized.length();

        for (int i = 0; i < length; i++) {
            TrieNode current = root;
            for (int j = i; j < length; j++) {
                char ch = normalized.charAt(j);
                current = current.children.get(ch);
                if (current == null) break;
                if (current.isEndOfWord) return true;
            }
        }
        return false;
    }

    public String censor(String text) {
        if (text == null || text.isBlank()) return text;

        String normalized = normalize(text);
        char[] result = text.toCharArray();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            TrieNode current = root;
            int matchEnd = -1;

            for (int j = i; j < length; j++) {
                char ch = normalized.charAt(j);
                current = current.children.get(ch);
                if (current == null) break;
                if (current.isEndOfWord) {
                    matchEnd = j;
                }
            }

            if (matchEnd != -1) {
                for (int k = i; k <= matchEnd; k++) {
                    result[k] = '*';
                }
                i = matchEnd; // Skip past the matched word
            }
        }
        return new String(result);
    }

    private String normalize(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            sb.append(LEET_MAP.getOrDefault(c, c));
        }
        return sb.toString();
    }

    private void addWord(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
    }
}