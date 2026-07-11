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

import java.util.List;

public class CommentMenu extends Menu {

    private Comment currentComment;
    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity = appHandler.getCurrentCommunity();

    public CommentMenu(Comment comment) {
        this.currentComment = comment;
        int menuIndex = 1;

        addOption(menuIndex++, "Reply", new ReplyToCommentCommand());
        addOption(menuIndex++, "Back to Post", new BackCommand());

        if (List.of(currentCommunity.getCommunityCreator().getUsername(),currentComment.getUsername(),"admin")
                .contains(appHandler.getCurrentUser().getUsername()))
            addOption(menuIndex++, "Delete comment", new DeleteCommentsCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());
        super.showMenu();
    }
}
