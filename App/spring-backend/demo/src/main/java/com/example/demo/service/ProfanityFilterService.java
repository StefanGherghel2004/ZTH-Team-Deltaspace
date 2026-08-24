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
import java.util.List;

@Service
public class ProfanityFilterService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    private RestClient restClient;

    private final TrieNode root = new TrieNode();

    // Multi-character substitutions, checked longest-first at each position.

    private static final List<Map.Entry<String, Character>> MULTI_CHAR_LEET = List.of(
            Map.entry("ph", 'f'),
            Map.entry("vv", 'w'),
            Map.entry("()", 'o'),
            Map.entry("|_|", 'u'),
            Map.entry("|)", 'd'),
            Map.entry("/\\", 'a'),
            Map.entry("|<", 'k'),
            Map.entry("|<|<", 'k')
    );

    // Single-character substitutions, applied after multi-char rules.
    private static final Map<Character, Character> LEET_MAP = Map.ofEntries(
            Map.entry('@', 'a'),
            Map.entry('4', 'a'),
            Map.entry('1', 'i'),
            Map.entry('!', 'i'),
            Map.entry('|', 'i'),
            Map.entry('0', 'o'),
            Map.entry('$', 's'),
            Map.entry('5', 's'),
            Map.entry('3', 'e'),
            Map.entry('+', 't'),
            Map.entry('7', 't'),
            Map.entry('8', 'b'),
            Map.entry('6', 'g'),
            Map.entry('9', 'g'),
            Map.entry('2', 'z')
    );

    @PostConstruct
    public void init() {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization","Bearer " + apiKey)
                .build();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("App/spring-backend/demo/profanity-words.txt")),
                StandardCharsets.UTF_8))) {
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


    private record CharSpan(char c, int start, int end) {}

    public boolean containsProfanity(String text) {
        return findFirstMatch(text) != null;
    }

    public String censor(String text){
        if(text==null || text.isBlank()) return text;
        String locallyCensored = censorLocal(text);
        try{
            return censorGroq(locallyCensored);
        }catch (Exception e) {
            Logger.severe("Failed to censor text using Groq: " + e.getMessage());
            return locallyCensored;
        }
    }

    public String censorGroq(String text){
        String prompt = """
You are a strict content moderation filter.
Your task is to replace any profane, offensive, or inappropriate words with ***.

PAY SPECIAL ATTENTION to evasion techniques:
1. Spaced-out words (e.g., "b a d w o r d" or "f u c k" must become "***" with the same number of * as the length of the word).
2. Obfuscated words using symbols, dots, hyphens, or underscores (e.g., "b.a.d.w.o.r.d", "f_u_c_k", "b-a-d").
3. Repeated characters or stretched words (e.g., "baaaadwooorrd", "f uuuu c k").
4. Leetspeak or character substitution (e.g., "b4dw0rd", "phuck").

Output Rules:
- Keep existing *** characters intact.
- Preserve sentence structure, capitalization, and surrounding non-profane words.
- Return ONLY the final censored text, without quotes, explanations, or introductory text.

Text: %s
""".formatted(text);
        Map<String , Object> requestBody = Map.of(
                "model",model,
                "temperature",0.2,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );
        try{
            GroqResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GroqResponse.class);
            if(response!=null && response.choices()!=null && !response.choices().isEmpty())
            {
                return response.choices().get(0).message().content().trim();
            }
        }catch(Exception e){
                Logger.severe("Failed to censor using Groq");
            }
        return text;

    }

    public String censorLocal(String text) {
        if (text == null || text.isBlank()) return text;

        List<CharSpan> spans = buildSpans(text);
        char[] result = text.toCharArray();

        int i = 0;
        while (i < spans.size()) {
            if (!isSpanWordStart(spans, i)) {
                i++;
                continue;
            }
            int matchEndSpanIdx = walkTrie(spans, i);
            if (matchEndSpanIdx != -1) {
                int from = spans.get(i).start();
                int to = spans.get(matchEndSpanIdx).end();
                for (int k = from; k <= to; k++) {
                    result[k] = '*';
                }
                i = matchEndSpanIdx + 1;
            } else {
                i++;
            }
        }
        return new String(result);
    }


    private int[] findFirstMatch(String text) {
        if (text == null || text.isBlank()) return null;

        List<CharSpan> spans = buildSpans(text);
        for (int i = 0; i < spans.size(); i++) {
            if (!isSpanWordStart(spans, i)) continue;
            int matchEndSpanIdx = walkTrie(spans, i);
            if (matchEndSpanIdx != -1) {
                return new int[]{spans.get(i).start(), spans.get(matchEndSpanIdx).end()};
            }
        }
        return null;
    }

    private int walkTrie(List<CharSpan> spans, int start) {
        TrieNode current = root;
        int matchEnd = -1;
        for (int j = start; j < spans.size(); j++) {
            current = current.children.get(spans.get(j).c());
            if (current == null) break;
            if (current.isEndOfWord && isSpanWordFinish(spans, j)) {
                matchEnd = j;
            }
        }
        return matchEnd;
    }


    private List<CharSpan> buildSpans(String text) {
        String lower = text.toLowerCase();
        List<CharSpan> raw = new ArrayList<>();

        int i = 0;
        int n = lower.length();
        outer:
        while (i < n) {
            for (var rule : MULTI_CHAR_LEET) {
                String pattern = rule.getKey();
                if (lower.regionMatches(i, pattern, 0, pattern.length())) {
                    raw.add(new CharSpan(rule.getValue(), i, i + pattern.length() - 1));
                    i += pattern.length();
                    continue outer;
                }
            }
            char c = lower.charAt(i);
            char mapped = LEET_MAP.getOrDefault(c, c);
            raw.add(new CharSpan(mapped, i, i));
            i++;
        }

        // Collapse runs of 3+ identical chars into a single span covering the whole run.
        List<CharSpan> collapsed = new ArrayList<>();
        int idx = 0;
        while (idx < raw.size()) {
            char c = raw.get(idx).c();
            int runStart = idx;
            while (idx < raw.size() && raw.get(idx).c() == c) {
                idx++;
            }
            int runLen = idx - runStart;
            if (runLen >= 3) {
                collapsed.add(new CharSpan(c, raw.get(runStart).start(), raw.get(idx - 1).end()));
            } else {
                for (int k = runStart; k < idx; k++) {
                    collapsed.add(raw.get(k));
                }
            }
        }
        return collapsed;
    }

    private boolean isSpanWordStart(List<CharSpan> spans, int index) {
        if (index == 0) return true;
        char prev = spans.get(index - 1).c();
        return !Character.isLetterOrDigit(prev);
    }

    private boolean isSpanWordFinish(List<CharSpan> spans, int index) {
        if (index == spans.size() - 1) return true;
        char next = spans.get(index + 1).c();
        return !Character.isLetterOrDigit(next);
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
    private record GroqResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String content) {}
}