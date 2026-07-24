package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.BackCommand;
import cli.backend.commands.postmenu.*;
import cli.backend.userinterface.views.UIPost;

public class EditPostMenu extends Menu{
    Post currentPost;

    public EditPostMenu(Post currentPost){
        this.currentPost = currentPost;

        setTitle("Edit post actions");
        addOption("Edit post contents", new EditPostCommand("contents"));
        addOption("Edit post nsfw tag", new EditPostCommand("nsfw"));
        addOption("Delete Post", new DeletePostCommand());
        addOption("Back to post", new BackCommand());
    }

    @Override
    public void showMenu() {
        UIPost.getInstance().showPostExpanded(currentPost);
        super.showMenu();
    }
}
