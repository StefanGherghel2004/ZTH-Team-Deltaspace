package org.example.commands.postmenu;

import org.example.commands.Command;
import org.example.handlers.AppHandler;

public class OpenEditPostMenuCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler.getInstance().setCurrentState(AppHandler.State.EDIT_POST);
        return true;
    }
}
