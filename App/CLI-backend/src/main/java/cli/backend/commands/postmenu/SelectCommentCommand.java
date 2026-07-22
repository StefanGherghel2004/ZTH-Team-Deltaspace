package cli.backend.commands.postmenu;

import cli.backend.Comment;
import cli.backend.commands.Command;
import cli.backend.database.CommentRepository;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;

public class SelectCommentCommand implements Command {
    private static CommentRepository commentRepository = CommentRepository.getInstance();

    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        Long commentId = console.getLongInput("Enter Comment ID to select: ");
        Comment foundComment = commentRepository
                .findCommentsByPostId(app.getCurrentPost().getId()).stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElse(null);

        if (foundComment != null) {
            app.setCurrentComment(foundComment);
            app.setCurrentState(AppHandler.State.ON_COMMENT);
        } else {
            console.error("Comment not found!");
        }

        return true;
    }
}