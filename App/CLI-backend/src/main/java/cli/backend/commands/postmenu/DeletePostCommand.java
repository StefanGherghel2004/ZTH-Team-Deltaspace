package cli.backend.commands.postmenu;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;
import cli.backend.services.PostService;
import cli.backend.services.CommunityService;

public class DeletePostCommand implements Command {
    @Override
    public boolean execute() {
       AppHandler app = AppHandler.getInstance();
       ConsoleReader consoleReader = ConsoleReader.getInstance();

        System.out.print("Are you sure you want to delete this post? (yes/no): ");
        String confirmation = consoleReader.readString();

        if (confirmation.equalsIgnoreCase("yes")) {
            PostService postService = PostService.getInstance();
           CommunityService communityService = CommunityService.getInstance();
            
           Post postToDelete = app.getCurrentPost();
            if(postToDelete.getUser().equals(app.getCurrentUser()))
            {
                postService.deletePost(postToDelete);
               String communityName = postToDelete.getCommunityName();
               Community community = communityService.getCommunityByName(communityName);
               if (community != null) {
                   if(postToDelete.getUser().equals(app.getCurrentUser()))
                   {
                       community.deletePost(postToDelete);
                   }
               }

               app.setCurrentPost(null);
               app.setCurrentState(AppHandler.State.LOGGED_IN);
               System.out.println("Post deleted successfully.");
               return true;
            }
            else{
                System.out.println("You cannot delete this post as you are not the owner.");
                app.setCurrentState(AppHandler.State.LOGGED_IN);
                return true;

            }
       } else {
           System.out.println("Post deletion cancelled.");
           app.setCurrentState(AppHandler.State.ON_POST);
           return true;
       }
    }
}
