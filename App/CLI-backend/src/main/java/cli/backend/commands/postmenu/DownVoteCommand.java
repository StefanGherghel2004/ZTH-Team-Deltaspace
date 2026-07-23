package cli.backend.commands.postmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.services.VoteService;
import cli.backend.userinterface.readers.Console;
import cli.backend.services.PostService;

public class DownVoteCommand implements Command {

    @Override
    public boolean execute() {

        AppHandler app = AppHandler.getInstance();
        VoteService voteService = VoteService.getInstance();

        voteService.downVote(app.getCurrentPost(),app.getCurrentUser());

        return true;

    }
}
