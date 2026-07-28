package cli.backend.userinterface.views;

import cli.backend.Community;
import cli.backend.User;
import cli.backend.handlers.AppHandler;
import cli.backend.services.CommunityService;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;
import cli.backend.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;

import static cli.backend.userinterface.textformatters.Theme.*;

public class UICommunity {

    private final static String HEADER_TITLE = "Communities";
    private final static String NO_COMMUNITIES = "No communities created.";

    private static final String FORMAT_COMMUNITY_SIMPLE = "%-12.12s | %s";

    private static UICommunity instance;
    private final Console console;
    private final static CommunityService communityService = CommunityService.getInstance();

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
        console.info(header(HEADER_TITLE));

        if (communities.isEmpty()) {
            console.info(NO_COMMUNITIES);
            return;
        }

        for (Community c : communities) {

            //
            //if (c.hasNSFWPost() && !user.checkAge()) {
            //    continue;
            //}

            showCommunitySimple(c);
        }

        console.info(footer());
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
        String NSFW=communityService.hasNSFWPosts(c);

        String comWithoutName = "Topic: " + formatTopic(c.getTopic()) + " | " + formatNSFW(NSFW);
        console.info(String.format(FORMAT_COMMUNITY_SIMPLE, c.getNickname(), comWithoutName));
    }
}
