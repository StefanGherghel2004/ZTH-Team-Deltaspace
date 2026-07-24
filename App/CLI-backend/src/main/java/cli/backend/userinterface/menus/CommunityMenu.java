package cli.backend.userinterface.menus;

import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.CreatePostCommand;
import cli.backend.commands.communitymenu.DeleteCommunityCommand;
import cli.backend.commands.communitymenu.EditCommunityCommand;
import cli.backend.commands.communitymenu.OpenEditCommunityCommand;
import cli.backend.commands.communitymenu.ShowPostsInCommunityCommand;
import cli.backend.handlers.AppHandler;

import java.util.List;

public class CommunityMenu extends Menu {

    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity;

    public CommunityMenu(Community currentCommunity) {
        this.currentCommunity = currentCommunity;

        setTitle(currentCommunity.getNickname());
        addOption("View Posts", new ShowPostsInCommunityCommand());
        addOption("Add Post", new CreatePostCommand());
        addOption("Return to Main Menu", new BackCommand());
        addOption("Edit Community",new OpenEditCommunityCommand());

        if (List.of(currentCommunity.getCommunityCreator(),"admin")
                .contains(appHandler.getCurrentUser().getId()))
            addOption("Delete community", new DeleteCommunityCommand());
    }

    @Override
    public void showMenu() {
        super.showMenu();
    }
}
