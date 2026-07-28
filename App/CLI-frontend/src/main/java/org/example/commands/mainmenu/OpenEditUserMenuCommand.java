package org.example.commands.mainmenu;


import org.example.commands.Command;
import org.example.handlers.AppHandler;

public class OpenEditUserMenuCommand implements Command {

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        appHandler.setCurrentState(AppHandler.State.EDIT_USER);
        return true;
    }
}
