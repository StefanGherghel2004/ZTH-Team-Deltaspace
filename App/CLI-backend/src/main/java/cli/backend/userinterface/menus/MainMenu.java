package cli.backend.userinterface.menus;

import cli.backend.commands.*;
import cli.backend.commands.mainmenu.*;
import cli.backend.commands.startmenu.DeleteUserCommand;

public class MainMenu extends Menu{

    public MainMenu() {
        setTitle("Main Menu");
        addOption("Show feed", new ShowFeedCommand());
        addOption("Create community", new CreateCommunityCommand());
        addOption("Create Post", new CreatePostCommand());
        addOption("Show communities", new ShowCommunitiesCommand());
        addOption("Edit Account", new OpenEditUserMenuCommand());
        addOption("Logout", new LogoutCommand());
    }

    @Override
    public void showMenu () {
        super.showMenu();
    }
}
