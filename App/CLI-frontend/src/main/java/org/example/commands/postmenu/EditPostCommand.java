package org.example.commands.postmenu;

import org.example.Post;
import org.example.commands.Command;
import org.example.handlers.AppHandler;

public class EditPostCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Post postToEdit = app.getCurrentPost();



        return true;
    }
}
