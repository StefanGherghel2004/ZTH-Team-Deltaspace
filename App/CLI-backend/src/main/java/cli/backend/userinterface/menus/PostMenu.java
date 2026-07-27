package cli.backend.userinterface.menus;

import cli.backend.Post;
import cli.backend.commands.*;
import cli.backend.commands.postmenu.*;
import cli.backend.handlers.AppHandler;
import cli.backend.services.PostService;
import cli.backend.userinterface.views.UIPost;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {
        this.currentPost = currentPost;
        PostService postService = PostService.getInstance();
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
        if(postService.canUserEditPost(appHandler.getCurrentUser(),currentPost))
            addOption("Edit Post", new OpenEditPostMenuCommand());
        if (postService.canUserDeletePost(appHandler.getCurrentUser(), appHandler.getCurrentCommunity()))
            addOption("Delete Post", new DeletePostCommand());

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
