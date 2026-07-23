package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;

public class OpenEditPostMenuCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler appHandler = AppHandler.getInstance();
        appHandler.setCurrentState(AppHandler.State.EDIT_POST);
        return true;
    }
}
