package cli.backend.userinterface.menus;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.postmenu.DeleteCommentCommand;
import cli.backend.commands.postmenu.ReplyToCommentCommand;
import cli.backend.handlers.AppHandler;
import cli.backend.services.CommentService;

public class CommentMenu extends Menu {

    private Comment currentComment;
    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity = appHandler.getCurrentCommunity();

    public CommentMenu(Comment comment) {
        this.currentComment = comment;

        setTitle("Selected Comment");
        addOption("Reply", new ReplyToCommentCommand());
        addOption("Back to Post", new BackCommand());

        CommentService commentService = CommentService.getInstance();

        if (commentService.canUserDeleteComment(appHandler.getCurrentUser(), currentComment, currentCommunity)) {
            addOption( "Delete comment", new DeleteCommentCommand());
        }
    }

    @Override
    public void showMenu() {
        super.showMenu();
    }
}
