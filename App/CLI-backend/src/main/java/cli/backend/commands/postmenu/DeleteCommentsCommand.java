package cli.backend.commands.postmenu;

import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.readers.ConsoleReader;

public class DeleteCommentsCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app=AppHandler.getInstance();
        ConsoleReader consoleReader = ConsoleReader.getInstance();

        return true;
    }
}
