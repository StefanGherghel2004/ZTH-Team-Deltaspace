package cli.backend.userinterface.menus;

import cli.backend.Community;
import cli.backend.commands.BackCommand;
import cli.backend.commands.CreatePostCommand;
import cli.backend.commands.communitymenu.DeleteCommunityCommand;
import cli.backend.commands.communitymenu.OpenEditCommunityCommand;
import cli.backend.commands.communitymenu.ShowPostsInCommunityCommand;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.views.UICommunity;

import java.util.List;

public class CommunityMenu extends Menu {

    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity;
    private UICommunity uiCommunity = UICommunity.getInstance();
    public CommunityMenu(Community currentCommunity) {
        this.currentCommunity = currentCommunity;

        setTitle("Community options");
        addOption("View Posts", new ShowPostsInCommunityCommand());
        addOption("Add Post", new CreatePostCommand());
        addOption("Return to Main Menu", new BackCommand());

        if (List.of(currentCommunity.getCommunityCreatorId(), "admin").contains(appHandler.getCurrentUser().getId())) {
            addOption("Edit Community", new OpenEditCommunityCommand());
        }
        if (List.of(currentCommunity.getCommunityCreatorId(),"admin")
                .contains(appHandler.getCurrentUser().getId()))
            addOption("Delete community", new DeleteCommunityCommand());
    }

    @Override
    public void showMenu() {
        uiCommunity.showCommunityExpanded(currentCommunity);
        super.showMenu();
    }
}
