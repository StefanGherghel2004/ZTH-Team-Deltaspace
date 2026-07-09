package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.CommentService;

public class AddCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        ConsoleReader consoleReader = ConsoleReader.getInstance();

        while (true) {
            System.out.print("Write Comment: ");
            String text = consoleReader.readString();
            try {
                CommentService.getInstance().addComment(app.getCurrentUser(), app.getCurrentPost(), text);
                System.out.println("Comment added successfully!");
                break;
            } catch (EmptyCommentException e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }
}
