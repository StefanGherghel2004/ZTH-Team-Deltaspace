package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.BackCommand;
import cli.backend.commands.postmenu.*;

public class EditPostMenu extends Menu{
    Post currentPost;

    public EditPostMenu(Post currentPost){
        this.currentPost = currentPost;

        setTitle("Edit post actions");
        addOption("Edit post contents", new EditPostCommand("contents"));
        addOption("Edit post nsfw tag", new EditPostCommand("nsfw"));
        addOption("Back to post", new BackCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("--- EDITING POST ---");
        System.out.println("Title:  " + currentPost.getPostTitle());
        System.out.println("NSFW:   " + (currentPost.isNSFW() ? "Yes" : "No"));
        System.out.println("Content: " + currentPost.getPostContents());
        System.out.println("--------------------");
        super.showMenu();
    }
}
