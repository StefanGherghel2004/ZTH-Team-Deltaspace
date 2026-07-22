package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.*;
import cli.backend.commands.postmenu.*;
import cli.backend.userinterface.views.UIPost;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;

        setTitle("Post Actions");
        addOption("Show comments", new ShowCommentsCommand());
        addOption("Add comment", new AddCommentCommand());
        addOption("Select comment (Reply)", new SelectCommentCommand());
        addOption("UpVote", new UpVoteCommand());
        addOption("DownVote",new DownVoteCommand());
        addOption("Delete Post", new DeletePostCommand());

        if(currentPost.getCommunityName().equalsIgnoreCase("u/" + currentPost.getAuthorUsername())) {
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
