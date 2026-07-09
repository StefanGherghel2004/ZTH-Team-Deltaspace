package cli.backend.userinterface;

import cli.backend.Community;

import java.util.ArrayList;
import java.util.List;

public class CommunityMenu extends Menu {

    private List<Community> communityList = new ArrayList<>();

    @Override
    public void showMenu() {
        System.out.println("\n--- Communities ---");
        if (communityList.isEmpty()) {
            System.out.println("No communities created");
        } else {
            for (Community c : communityList)
                System.out.println(c.getNickname());
        }

        System.out.print("\nChoose a community (or press Enter to go back): ");
    }

    public void setCommunityList (List<Community> communityList) {

        this.communityList = communityList;
    }
}
