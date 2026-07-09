package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommentService;

public class ReplyToCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        ConsoleReader reader = ConsoleReader.getInstance();

        System.out.print("Write reply: ");
        String text = reader.readString();

        try {
            CommentService.getInstance().replyToComment(
                    app.getCurrentUser(), app.getCurrentPost(), app.getCurrentComment(), text);
            System.out.println("Reply added successfully!");
        } catch (EmptyCommentException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}