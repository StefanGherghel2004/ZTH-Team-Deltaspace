package org.example.commands.postmenu;

import org.example.Post;
import org.example.commands.Command;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;

import java.awt.*;
import java.net.URI;

public class OpenImageCommand implements Command {

    @Override
    public boolean execute() {
        Post post = AppHandler.getInstance().getCurrentPost();

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(post.getImageUrl()));
            }
        } catch (Exception e) {
            Console.getInstance().error("Could not open browser");
        }
        return true;
    }
}
