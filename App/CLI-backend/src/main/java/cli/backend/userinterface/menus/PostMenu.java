package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.*;
import cli.backend.commands.postmenu.AddCommentCommand;
import cli.backend.commands.postmenu.DeletePostCommand;
import cli.backend.commands.postmenu.SelectCommentCommand;
import cli.backend.commands.postmenu.ShowCommentsCommand;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;
        int menuIndex = 1;

        addOption(menuIndex++, "Show comments", new ShowCommentsCommand());
        addOption(menuIndex++, "Add comment", new AddCommentCommand());
        addOption(menuIndex++, "Select comment (Reply)", new SelectCommentCommand());
        addOption(menuIndex++,"Delete Post", new DeletePostCommand());

        if(currentPost.getCommunityName().equalsIgnoreCase("u/" + currentPost.getUser().getUsername())) {
            addOption(menuIndex++, "Back to Main Menu", new BackCommand());
        } else {
            addOption(menuIndex++, "Back to Community", new BackCommand());
        }
    }

    @Override
    public void showMenu() {
        System.out.println("ID:        " + currentPost.getPostID());
        System.out.println("Community: " + currentPost.getCommunityName());
        System.out.println("Author:    u/" + currentPost.getUser().getUsername());
        System.out.println("Title:     " + currentPost.getPostTitle());
        System.out.println("NSFW:      " + (currentPost.getNSFW() ? "Yes" : "No"));
        System.out.println(currentPost.getPostContents());
        System.out.println("\n--- Post Actions ---");
        super.showMenu();
    }
}
