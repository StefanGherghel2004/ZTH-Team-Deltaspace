package cli.backend.commands.startmenu;


import cli.backend.commands.Command;
import cli.backend.readers.Console;

public class QuitCommand implements Command {
    @Override
    public boolean execute() {
        Console.getInstance().info("Logging out...");
        return false;
    }
}
