package org.example.userinterface.menus;

import org.example.Community;
import org.example.commands.BackCommand;
import org.example.commands.CreatePostCommand;
import org.example.commands.communitymenu.DeleteCommunityCommand;
import org.example.commands.communitymenu.OpenEditCommunityCommand;
import org.example.commands.communitymenu.ShowPostsInCommunityCommand;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UICommunity;

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

        if (List.of(currentCommunity.getCommunityCreatorId(), "admin").contains(appHandler.getCurrentUser().getId())) {
            addOption("Edit Community", new OpenEditCommunityCommand());
        }
        if (List.of(currentCommunity.getCommunityCreatorId(),"admin")
                .contains(appHandler.getCurrentUser().getId()))
            addOption("Delete community", new DeleteCommunityCommand());

        addOption("Return to Main Menu", new BackCommand());
    }

    @Override
    public void showMenu() {
        uiCommunity.showCommunityExpanded(currentCommunity);
        super.showMenu();
    }
}
