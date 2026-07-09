package cli.backend.commands.startmenu;


import cli.backend.commands.Command;

public class QuitCommand implements Command {
    @Override
    public boolean execute() {
        System.out.println("Shutting down...");
        return false;
    }
}
