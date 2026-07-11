package cli.backend.userinterface.menus;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.communitymenu.DeleteCommunityCommand;
import cli.backend.commands.postmenu.DeleteCommentsCommand;
import cli.backend.commands.postmenu.ReplyToCommentCommand;
import cli.backend.handlers.AppHandler;
import cli.backend.services.CommentService;
import cli.backend.services.CommunityService;

public class CommentMenu extends Menu {

    private Comment currentComment;
    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity = appHandler.getCurrentCommunity();

    public CommentMenu(Comment comment) {
        this.currentComment = comment;

        addOption(1, "Reply", new ReplyToCommentCommand());
        addOption(2, "Back to Post", new BackCommand());

        if ((appHandler.getCurrentUser() == currentCommunity.getCommunityCreator()) ||
                (appHandler.getCurrentUser() == currentComment.getUser()))
            addOption(3, "Delete comment", new DeleteCommentsCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());
        super.showMenu();
    }
}
