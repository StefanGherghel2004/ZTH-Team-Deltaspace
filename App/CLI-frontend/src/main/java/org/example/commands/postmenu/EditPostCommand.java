package org.example.commands.postmenu;

import org.example.Post;
import org.example.apiclients.PostApiClient;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

public class EditPostCommand implements Command {

    private final String editType;
    private final PostApiClient postApiClient = PostApiClient.getInstance();

    public EditPostCommand(String editType) {
        this.editType = editType != null ? editType : "";
    }

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Post postToEdit = app.getCurrentPost();

        try {

            Map<String, Object> updateFields = new HashMap<>();

            updateFields.put("title", postToEdit.getTitle());
            updateFields.put("content", postToEdit.getContent());

            switch (editType) {
                case "title" -> {
                    String newTitle = console.getStringInput("Please enter the new title:");
                    updateFields.put("title", newTitle);
                }
                case "content" -> {
                    String newContent = console.getMultiLineInput("Please enter the new content:");
                    updateFields.put("content", newContent);
                }
                default -> {
                    console.error("Invalid edit operation: " + editType);
                    return true;
                }
            }


            Post updatedPost = postApiClient.updatePost(postToEdit.getId(), updateFields, app.getJwtToken());

            if (updatedPost != null) {
                console.success("Post updated successfully!");
                app.setCurrentPost(updatedPost);
            } else {
                console.error("Failed to update post on the server.");
            }

        } catch (BackNavigationException backNavigationException) {
            console.info(backNavigationException.getMessage());
        } catch (HttpClientErrorException e) {
            console.error(e.getMessage());
        } catch (Exception e) {
            console.error("Network error occurred while updating the post: " + e.getMessage());
        }

        return true;
    }
}
