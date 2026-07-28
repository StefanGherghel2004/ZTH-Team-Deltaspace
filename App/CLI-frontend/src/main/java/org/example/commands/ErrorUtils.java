package org.example.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern KEY_PATTERN = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\)");

    public static String extractDuplicateKeyError(HttpClientErrorException.Conflict exception) {
        try {
            String jsonResponseBody = exception.getResponseBodyAsString();
            JsonNode root = mapper.readTree(jsonResponseBody);

            JsonNode detailsArray = root.path("error").path("details");
            if (detailsArray.isArray() && !detailsArray.isEmpty()) {
                String dbMessage = detailsArray.get(0).path("message").asText();

                Matcher matcher = KEY_PATTERN.matcher(dbMessage);
                if (matcher.find()) {
                    String field = matcher.group(1);
                    String value = matcher.group(2);

                    String capitalizedField = field.substring(0, 1).toUpperCase() + field.substring(1);

                    return capitalizedField + " '" + value + "' is already registered!";
                }
            }
        } catch (Exception e) {
        }
        return "An account with these details already exists!";
    }
}