package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.*;
import cli.backend.commands.postmenu.*;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;
        int menuIndex = 1;

        setTitle("Post Actions");
        addOption(menuIndex++, "Show comments", new ShowCommentsCommand());
        addOption(menuIndex++, "Add comment", new AddCommentCommand());
        addOption(menuIndex++, "Select comment (Reply)", new SelectCommentCommand());
        addOption(menuIndex++,"UpVote", new UpVoteCommand());
        addOption(menuIndex++,"DownVote",new DownVoteCommand());
        addOption(menuIndex++,"Delete Post", new DeletePostCommand());

        if(currentPost.getCommunityName().equalsIgnoreCase("u/" + currentPost.getAuthorUsername())) {
            addOption(menuIndex++, "Back to Main Menu", new BackCommand());
        } else {
            addOption(menuIndex++, "Back to Community", new BackCommand());
        }
    }

    @Override
    public void showMenu() {
        System.out.println("ID:        " + currentPost.getId());
        System.out.println("Community: " + currentPost.getCommunityName());
        System.out.println("Author:    u/" + currentPost.getAuthorUsername());
        System.out.println("Title:     " + currentPost.getPostTitle());
        System.out.println("NSFW:      " + (currentPost.isNSFW() ? "Yes" : "No"));
        System.out.println("UpVotes:   " + currentPost.getUpVotes());
        System.out.println("DownVotes: " + currentPost.getDownVotes());
        System.out.println(currentPost.getPostContents());
        super.showMenu();
    }
}
