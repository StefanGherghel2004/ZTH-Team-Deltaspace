package org.example.userinterface.menus;

import org.example.Community;
import org.example.commands.BackCommand;
import org.example.commands.CreatePostCommand;
import org.example.commands.communitymenu.*;
import org.example.handlers.AppHandler;
import org.example.userinterface.views.UICommunity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommunityMenu extends Menu {

    private AppHandler appHandler = AppHandler.getInstance();
    private Community currentCommunity;
    private UICommunity uiCommunity = UICommunity.getInstance();
    public CommunityMenu(Community currentCommunity) {
        this.currentCommunity = currentCommunity;

        setTitle("Community options");
        addOption("View Posts", new ShowPostsInCommunityCommand());
        addOption("Add Post", new CreatePostCommand());

        if(appHandler.getCurrentUser()!=null && currentCommunity!=null){
            UUID currentUserId = appHandler.getCurrentUser().getId();
            UUID creatorId = currentCommunity.getCommunityCreatorId();

        if (creatorId != null && Objects.equals(creatorId, currentUserId)) {
            addOption("Edit Community", new OpenEditCommunityCommand());
            addOption("Delete community", new DeleteCommunityCommand());
        }
        addOption("Join Communtiy", new JoinCommunityCommand());
        addOption("Leave Community", new LeaveCommunityCommand());
    }

    addOption("Return to Main Menu", new BackCommand());
}

    @Override
    public void showMenu() {
        uiCommunity.showCommunityExpanded(currentCommunity);
        super.showMenu();
    }
}
