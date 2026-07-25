package cli.backend.userinterface.views;

import cli.backend.Community;
import cli.backend.User;
import cli.backend.handlers.AppHandler;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;

import static cli.backend.userinterface.textformatters.Theme.MAX_TEXT_WIDTH;
import static cli.backend.userinterface.textformatters.Theme.formatTopic;

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
    public void showCommunityExpanded(Community c){
        String title = c.getNickname();
        List<String> lines=new ArrayList<>();
        AppHandler app = AppHandler.getInstance();
        User user = app.getCurrentUser();

        lines.add("Topic: "+ formatTopic(c.getTopic()));
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(c.getDescription(),MAX_TEXT_WIDTH);
        lines.add("Description: ");
        lines.addAll(wrappedContent);
        lines.add("");

        String boxedCommunity= BoxPadder.format(lines,title);
        console.info(boxedCommunity);
    }

    public void showCommunitySimple(Community c) {
        console.info(c.getNickname() + " | Topic: " + formatTopic(c.getTopic()));
    }
}
