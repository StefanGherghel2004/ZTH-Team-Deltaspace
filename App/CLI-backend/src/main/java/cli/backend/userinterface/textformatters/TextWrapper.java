package cli.backend.userinterface.textformatters;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {


    // Wraps a given text into multiple lines based on a maximum width constraint
    public static List<String> wrap(String text, int maxWidth) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        String[] paragraphs = text.split("\n");

        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                result.add("");
                continue;
            }

            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                // one word bigger than the limit => slice it until it fits
                while (word.length() > maxWidth) {
                    if (!currentLine.isEmpty()) {
                        result.add(currentLine.toString());
                        currentLine.setLength(0);
                    }
                    result.add(word.substring(0, maxWidth));
                    word = word.substring(maxWidth);
                }

                // if adding current word exceeds the limit => save current line and begin a new one
                if (currentLine.length() + word.length() + 1 > maxWidth) {
                    result.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    if (!currentLine.isEmpty()) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }

            if (!currentLine.isEmpty()) {
                result.add(currentLine.toString());
            }
        }

        return result;
    }

}
