package cli.backend.userinterface;

import cli.backend.Community;

public class InCommunityMenu extends Menu {

    Community currentCommunity;

    @Override
    public void showMenu () {

        System.out.println("\n--- " + currentCommunity.getNickname() + " ---");
        System.out.println("1. View Posts");
        System.out.println("2. Add Post");
        System.out.println("3. Return to Main Menu");
    }

    public void setCurrentCommunity (Community community) {

        this.currentCommunity = community;
    }
}
