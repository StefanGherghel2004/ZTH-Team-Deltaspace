package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.exceptions.BackNavigationException;
import cli.backend.exceptions.EmptyCommentException;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.CommentService;

public class AddCommentCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        try {
            while (true) {
                String text = console.getMultiLineInput("Write comment: ");
                try {
                    CommentService.getInstance().addComment(app.getCurrentUser().getUsername(), app.getCurrentPost(), text);
                    console.success("Comment added successfully!");
                    break;
                } catch (EmptyCommentException e) {
                    console.error(e.getMessage());
                }
            }
        }
        catch (BackNavigationException backNavigationException){
            console.info(backNavigationException.getMessage());
        }
        return true;
    }
}
