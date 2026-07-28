package org.example.userinterface.menus;

import org.example.Post;
import org.example.commands.BackCommand;
import org.example.commands.postmenu.*;
import org.example.userinterface.views.UIPost;

public class EditPostMenu extends Menu{
    Post currentPost;

    public EditPostMenu(Post currentPost){
        this.currentPost = currentPost;

        setTitle("Edit post actions");
        addOption("Edit post title", new EditPostCommand("title"));
        addOption("Edit post content", new EditPostCommand("content"));
        addOption("Back to post", new BackCommand());
    }

    @Override
    public void showMenu() {
        UIPost.getInstance().showPostExpanded(currentPost);
        super.showMenu();
    }
}
