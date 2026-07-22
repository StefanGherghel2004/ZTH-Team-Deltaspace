package cli.backend.commands.postmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.Console;
import cli.backend.services.PostService;

import java.util.List;

public class UpVoteCommand implements Command {

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        Post currentPost = app.getCurrentPost();
        PostService postService = PostService.getInstance();
        Integer upvotes = currentPost.getUpVotes();

        postService.upVote(app.getCurrentPost(),app.getCurrentUser());

        return true;

    }
}
