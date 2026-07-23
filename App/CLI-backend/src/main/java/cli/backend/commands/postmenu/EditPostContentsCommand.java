package cli.backend.commands.postmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.database.PostRepository;
import cli.backend.handlers.AppHandler;
import cli.backend.services.PostService;
import cli.backend.userinterface.readers.Console;

public class EditPostContentsCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();
        PostService postService = PostService.getInstance();
        PostRepository postRepository = PostRepository.getInstance();

        Post postToEdit = app.getCurrentPost();

        if (!postService.canUserEditPost(app.getCurrentUser(),postToEdit)) {
            console.error("You cannot edit this post as you are not the owner.");
            app.setCurrentState(AppHandler.State.ON_POST);
            return true;
        }

        String newContents = console.getStringInput("Please enter your new contents for this post:",
                false);

        postToEdit.setPostContents(newContents);
        postRepository.updatePost(postToEdit);
        app.setCurrentState(AppHandler.State.ON_POST);
        return true;
    }
}
