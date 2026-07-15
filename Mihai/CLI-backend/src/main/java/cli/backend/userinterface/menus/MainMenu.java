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
        addOption(1, "Show feed", new ShowFeedCommand());
        addOption(2, "Create community", new CreateCommunityCommand());
        addOption(3, "Create Post", new CreatePostCommand());
        addOption(4, "Show communities", new ShowCommunitiesCommand());
        addOption(5, "Logout", new LogoutCommand());
        addOption(6,"Delete Account",new DeleteUserCommand());
    }

    @Override
    public void showMenu () {
        super.showMenu();
    }
}
