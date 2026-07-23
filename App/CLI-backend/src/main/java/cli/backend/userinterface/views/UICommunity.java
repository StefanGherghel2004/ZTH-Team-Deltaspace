package cli.backend.userinterface.views;

import cli.backend.Community;
import cli.backend.User;
import cli.backend.userinterface.readers.Console;

import java.util.List;

public class UICommunity {

    private static UICommunity instance;
    private final Console console;

    private UICommunity() {
        this.console = Console.getInstance();
    }

    public static UICommunity getInstance() {
        if (instance == null) {
            instance = new UICommunity();
        }
        return instance;
    }

    public void showCommunitiesList(List<Community> communities, User user) {
        console.info("\n--- Communities ---");

        if (communities.isEmpty()) {
            console.info("No communities created.");
            return;
        }

        for (Community c : communities) {

            //
            //if (c.hasNSFWPost() && !user.checkAge()) {
            //    continue;
            //}

            showCommunitySimple(c);
        }
    }

    public void showCommunitySimple(Community c) {
        console.info(c.getNickname() + " | Topic: " + c.getTopic());
    }
}
