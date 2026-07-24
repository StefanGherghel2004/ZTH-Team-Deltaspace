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
        addOption("Logout", new LogoutCommand());
        addOption("Edit Account", new OpenEditUserMenuCommand());
        addOption("Delete Account",new DeleteUserCommand());
    }

    @Override
    public void showMenu () {
        super.showMenu();
    }
}
