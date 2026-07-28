package org.example.userinterface.menus;

import org.example.Post;
import org.example.commands.BackCommand;
import org.example.commands.postmenu.*;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UIPost;
import org.example.util.AuthorizationService;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;
        AppHandler appHandler = AppHandler.getInstance();

        setTitle("Post Actions");
        if (currentPost.getImageUrl() != null) {
            addOption("Open Image", new OpenImageCommand());
        }
        addOption("Show comments", new ShowCommentsCommand());
        addOption("Add comment", new AddCommentCommand());
        addOption("Select comment (Reply)", new SelectCommentCommand());
        addOption("UpVote", new UpVoteCommand());
        addOption("DownVote",new DownVoteCommand());
        if (AuthorizationService.canUserEditPost(appHandler.getCurrentUser(), currentPost)) {
            addOption("Edit Post", new OpenEditPostMenuCommand());
        }

        if (AuthorizationService.canUserDeletePost(appHandler.getCurrentUser(), currentPost, appHandler.getCurrentCommunity())) {
            addOption("Delete Post", new DeletePostCommand());
        }

        if(currentPost.getSubreddit() == null) {
            addOption("Back to Main Menu", new BackCommand());
        } else {
            addOption("Back to Community", new BackCommand());
        }
    }

    @Override
    public void showMenu() {
        UIPost.getInstance().showPostExpanded(currentPost);
        super.showMenu();
    }
}
