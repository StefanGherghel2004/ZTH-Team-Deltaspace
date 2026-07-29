package org.example.commands.postmenu;

import org.example.Comment;
import org.example.commands.Command;
import org.example.exceptions.BackNavigationException;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.example.userinterface.views.UIComment;

public class SelectCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        UIComment uiComment = UIComment.getInstance();

        try {
            int selectedIndex = console.getIntInput("Enter comment number to select: ");

            if (!uiComment.isValidIndex(selectedIndex)) {
                console.error("Invalid comment number! Please select a valid index from the thread.");
                return true;
            }

            Comment selectedComment = uiComment.getCommentByIndex(selectedIndex);

            app.setCurrentComment(selectedComment);
            app.setCurrentState(AppHandler.State.ON_COMMENT);
            console.success("Selected comment by @" + selectedComment.getAuthorUsername());

        } catch (BackNavigationException e) {
            console.info(e.getMessage());
        } catch (NumberFormatException e) {
            console.error("Please enter a valid numeric comment index.");
        }
        return true;
    }
}