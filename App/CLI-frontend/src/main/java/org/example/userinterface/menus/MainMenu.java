package org.example.userinterface.menus;

import org.example.commands.CreatePostCommand;
import org.example.commands.mainmenu.*;

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
