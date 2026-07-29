package org.example.userinterface.menus;

import org.example.Comment;
import org.example.Community;
import org.example.User;
import org.example.apiclients.CommentApiClient;
import org.example.commands.BackCommand;
import org.example.commands.postmenu.DeleteCommentCommand;
import org.example.commands.postmenu.ReplyToCommentCommand;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UIComment;

public class CommentMenu extends Menu {

    private Comment currentComment;
    private User currentUser;
    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity = appHandler.getCurrentCommunity();
    private CommentApiClient commentApiClient = CommentApiClient.getInstance();

    public CommentMenu(Comment comment) {
        this.currentComment = comment;
        currentComment = commentApiClient.getCommentById(currentComment.getId(),appHandler.getJwtToken());
        currentUser = appHandler.getCurrentUser();
        setTitle("Selected Comment");
        addOption("Reply", new ReplyToCommentCommand());
        if (currentComment.getAuthorUsername().equals(currentUser.getUsername())) {
            addOption( "Delete comment", new DeleteCommentCommand());
        }
        addOption("Back to Post", new BackCommand());

    }

    @Override
    public void showMenu() {
        UIComment.getInstance().showComment(currentComment);
        super.showMenu();
    }
}
