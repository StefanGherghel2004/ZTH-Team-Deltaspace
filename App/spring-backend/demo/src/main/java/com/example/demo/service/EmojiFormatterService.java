package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class EmojiFormatterService {

    private final Map<String, String> emojiDictionary;

    public EmojiFormatterService() {
        emojiDictionary = new LinkedHashMap<>();
        emojiDictionary.put(";)!", "😉");
        emojiDictionary.put(";)", "😉");
        emojiDictionary.put(":)", "🙂");
        emojiDictionary.put(":(", "🙁");
        emojiDictionary.put(":D", "😁");
        emojiDictionary.put("<3", "❤️");
        emojiDictionary.put(":O", "😲");
        emojiDictionary.put(":-)", "🙂");
    }

    public String format(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String formattedText = text;
        for (Map.Entry<String, String> entry : emojiDictionary.entrySet()) {
            formattedText = formattedText.replace(entry.getKey(), entry.getValue());
        }

        return formattedText;
    }
}
