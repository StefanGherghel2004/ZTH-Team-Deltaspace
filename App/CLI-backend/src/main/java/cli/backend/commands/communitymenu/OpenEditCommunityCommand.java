package cli.backend.commands.communitymenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.menus.Menu;

public class OpenEditCommunityCommand implements Command {


    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        appHandler.setCurrentState(AppHandler.State.EDIT_COMMUNITY);
        return true;
    }


}
