package org.example.userinterface.menus;

import org.example.Community;
import org.example.commands.BackCommand;
import org.example.commands.communitymenu.EditCommunityCommand;

public class EditCommunityMenu extends  Menu{
    Community currentCommunity;
    public EditCommunityMenu(Community currentCommunity){
        this.currentCommunity=currentCommunity;

        setTitle("Edit Community Menu");
//        addOption("Edit Name", new EditCommunityCommand("name"));
//        addOption("Edit Topic" , new EditCommunityCommand("topic"));
//        addOption("Edit Description",new EditCommunityCommand("description"));
//        addOption("Return to Community", new BackCommand());
    }

}
