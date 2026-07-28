package org.example.userinterface.menus;

import org.example.Post;
import org.example.commands.BackCommand;
import org.example.commands.postmenu.*;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UIPost;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;
        AppHandler appHandler = AppHandler.getInstance();

        setTitle("Post Actions");
        if (currentPost.getImageLink() != null) {
            addOption("Open Image", new OpenImageCommand());
        }
        addOption("Show comments", new ShowCommentsCommand());
        addOption("Add comment", new AddCommentCommand());
        addOption("Select comment (Reply)", new SelectCommentCommand());
        addOption("UpVote", new UpVoteCommand());
        addOption("DownVote",new DownVoteCommand());
//        if(postService.canUserEditPost(appHandler.getCurrentUser(),currentPost))
//            addOption("Edit Post", new OpenEditPostMenuCommand());
//        if (postService.canUserDeletePost(appHandler.getCurrentUser(), appHandler.getCurrentCommunity()))
//            addOption("Delete Post", new DeletePostCommand());

        if(currentPost.getCommunityName() == null) {
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
