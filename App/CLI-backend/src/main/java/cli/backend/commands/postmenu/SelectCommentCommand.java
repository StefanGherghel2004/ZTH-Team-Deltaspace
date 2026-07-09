package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;

public class SelectCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        ConsoleReader consoleReader = ConsoleReader.getInstance();

        System.out.print("Enter Comment ID to select: ");
        try {
            int commentId = Integer.parseInt(consoleReader.readString());
            Comment foundComment = app.getCurrentPost().findCommentById(commentId);

            if (foundComment != null) {
                app.setCurrentComment(foundComment);
                app.setCurrentState(AppHandler.State.ON_COMMENT);
            } else {
                System.out.println("Comment not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
        return true;
    }
}