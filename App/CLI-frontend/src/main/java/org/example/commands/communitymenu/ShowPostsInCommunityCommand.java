package org.example.commands.communitymenu;

import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

import java.util.List;

public class ShowPostsInCommunityCommand implements Command {
    @Override
    public boolean execute() {
        AppHandler app = AppHandler.getInstance();
        Console console = Console.getInstance();

        return true;
    }
}