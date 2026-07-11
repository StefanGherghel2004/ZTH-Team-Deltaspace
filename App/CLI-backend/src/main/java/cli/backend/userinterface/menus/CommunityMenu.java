package cli.backend.userinterface.menus;

import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.CreatePostCommand;
import cli.backend.commands.communitymenu.DeleteCommunityCommand;
import cli.backend.commands.communitymenu.ShowPostsInCommunityCommand;
import cli.backend.handlers.AppHandler;

public class CommunityMenu extends Menu {

    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity;

    public CommunityMenu(Community currentCommunity) {
        this.currentCommunity = currentCommunity;

        addOption(1, "View Posts", new ShowPostsInCommunityCommand());
        addOption(2, "Add Post", new CreatePostCommand());
        addOption(3, "Return to Main Menu", new BackCommand());

        if (appHandler.getCurrentUser() == currentCommunity.getCommunityCreator())
            addOption(4, "Delete community", new DeleteCommunityCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + currentCommunity.getNickname() + " ---");
        super.showMenu();
    }
}
