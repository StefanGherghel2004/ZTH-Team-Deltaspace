package org.example.commands.startmenu;

import org.example.commands.Command;
import org.example.userinterface.readers.Console;

public class QuitCommand implements Command {
    @Override
    public boolean execute() {
        Console.getInstance().info("Logging out...");
        return false;
    }
}
