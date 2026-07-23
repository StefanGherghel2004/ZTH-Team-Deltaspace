package cli.backend.commands.postmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.database.PostRepository;
import cli.backend.handlers.AppHandler;
import cli.backend.services.PostService;
import cli.backend.userinterface.readers.Console;

public class EditPostCommand implements Command {
    String editType;

    public EditPostCommand(String editType){
        this.editType = editType != null ? editType : "";
    }

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
        switch (editType) {
            case "contents" -> {
                String newContents = console.getStringInput(
                        "Please enter your new contents for this post:",
                        false);
                postToEdit.setPostContents(newContents);
            }

            case "nsfw" -> {
                boolean newNsfw = console.getUserConfirmation("Is your post NSFW? [yes/no]");
                postToEdit.setNSFW(newNsfw);
            }

            default -> {
                console.error("Invalid edit operation: " + editType);
                app.setCurrentState(AppHandler.State.ON_POST);
                return true;
            }
        }
        boolean updated = postRepository.updatePost(postToEdit);

        if (updated) {
            console.success("Post updated successfully!");
        } else {
            console.error("Failed to update post in database.");
        }

        app.setCurrentState(AppHandler.State.ON_POST);
        return true;
    }
}
