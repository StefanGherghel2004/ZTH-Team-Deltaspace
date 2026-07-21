package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommentService;

public class ReplyToCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        String text = console.getStringInput("Write reply");

        try {
            CommentService.getInstance().replyToComment(
                    app.getCurrentUser().getUsername(), app.getCurrentPost(), app.getCurrentComment(), text);
            console.success("Reply added successfully!");
        } catch (EmptyCommentException e) {
            console.error(e.getMessage());
        }
        return true;
    }
}