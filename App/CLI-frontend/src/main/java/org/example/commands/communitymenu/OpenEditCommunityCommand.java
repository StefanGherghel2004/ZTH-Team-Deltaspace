package org.example.commands.communitymenu;


import org.example.commands.Command;
import org.example.handlers.AppHandler;

public class OpenEditCommunityCommand implements Command {


    @Override
    public boolean execute() {

        AppHandler appHandler = AppHandler.getInstance();
        appHandler.setCurrentState(AppHandler.State.EDIT_COMMUNITY);
        return true;
    }


}
