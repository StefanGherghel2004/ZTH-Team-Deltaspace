package org.example.userinterface.menus;

import org.example.Comment;
import org.example.Community;
import org.example.commands.BackCommand;
import org.example.commands.postmenu.ReplyToCommentCommand;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UIComment;

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
        UIComment.getInstance().showComment(currentComment);
        super.showMenu();
    }
}
