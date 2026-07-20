package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.services.CommentService;

public class AddCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        while (true) {
            String text = console.getStringInput("Write comment: ");
            try {
                CommentService.getInstance().addComment(app.getCurrentUser(), app.getCurrentPost(), text);
                console.success("Comment added successfully!");
                break;
            } catch (EmptyCommentException e) {
                console.error(e.getMessage());
            }
        }
        return true;
    }
}
