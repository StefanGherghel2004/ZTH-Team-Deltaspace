package cli.backend.commands.mainmenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;

public class OpenEditUserMenuCommand implements Command {

    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        appHandler.setCurrentState(AppHandler.State.EDIT_USER);
        return true;
    }
}
