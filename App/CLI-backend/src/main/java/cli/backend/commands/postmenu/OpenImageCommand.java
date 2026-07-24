package cli.backend.commands.postmenu;

import cli.backend.Post;
import cli.backend.commands.Command;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;

import java.awt.*;
import java.net.URI;

public class OpenImageCommand implements Command {

    @Override
    public boolean execute() {
        Post post = AppHandler.getInstance().getCurrentPost();

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(post.getImageLink()));
            }
        } catch (Exception e) {
            Console.getInstance().error("Could not open browser");
        }
        return true;
    }
}
