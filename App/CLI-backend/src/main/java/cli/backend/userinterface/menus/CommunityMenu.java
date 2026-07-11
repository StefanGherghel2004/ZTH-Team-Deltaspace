package cli.backend.userinterface.menus;

import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.CreatePostCommand;
import cli.backend.commands.communitymenu.DeleteCommunityCommand;
import cli.backend.commands.communitymenu.ShowPostsInCommunityCommand;
import cli.backend.handlers.AppHandler;

import java.util.List;

public class CommunityMenu extends Menu {

    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity;

    public CommunityMenu(Community currentCommunity) {
        this.currentCommunity = currentCommunity;
        int menuIndex = 1;

        addOption(menuIndex++, "View Posts", new ShowPostsInCommunityCommand());
        addOption(menuIndex++, "Add Post", new CreatePostCommand());
        addOption(menuIndex++, "Return to Main Menu", new BackCommand());

        if (List.of(currentCommunity.getCommunityCreator().getUsername(),"admin")
                .contains(appHandler.getCurrentUser().getUsername()))
            addOption(menuIndex++, "Delete community", new DeleteCommunityCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- " + currentCommunity.getNickname() + " ---");
        super.showMenu();
    }
}
