package cli.backend.userinterface.menus;

import cli.backend.commands.*;
import cli.backend.commands.mainmenu.CreateCommunityCommand;
import cli.backend.commands.mainmenu.LogoutCommand;
import cli.backend.commands.mainmenu.ShowCommunitiesCommand;
import cli.backend.commands.mainmenu.ShowFeedCommand;
import cli.backend.commands.startmenu.DeleteUserCommand;

public class MainMenu extends Menu{

    public MainMenu() {
        setTitle("Main Menu");
        addOption("Show feed", new ShowFeedCommand());
        addOption("Create community", new CreateCommunityCommand());
        addOption("Create Post", new CreatePostCommand());
        addOption("Show communities", new ShowCommunitiesCommand());
        addOption("Logout", new LogoutCommand());
        addOption("Delete Account",new DeleteUserCommand());
    }

    @Override
    public void showMenu () {
        super.showMenu();
    }
}
