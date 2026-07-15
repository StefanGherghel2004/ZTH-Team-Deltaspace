package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;

public class SelectCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        int commentId = console.getIntInput("Enter Comment ID to select: ");
        Comment foundComment = app.getCurrentPost().findCommentById(commentId);

        if (foundComment != null) {
            app.setCurrentComment(foundComment);
            app.setCurrentState(AppHandler.State.ON_COMMENT);
        } else {
            console.error("Comment not found!");
        }

        return true;
    }
}