package cli.backend.userinterface.menus;

import cli.backend.Community;
import cli.backend.services.CommunityService;

public class CommunityMenu extends Menu {

    private CommunityService communityService = CommunityService.getInstance();

    @Override
    public void showMenu() {
        System.out.println("\n--- Communities ---");
        if (communityService.getCommunities().isEmpty()) {
            System.out.println("No communities created");
        } else {
            for (Community c : communityService.getCommunities())
                System.out.println(c.getNickname());
        }

        System.out.print("\nChoose a community (or press Enter to go back): ");
    }
}
