package org.example.userinterface.views;

import org.example.Community;
import org.example.User;
import org.example.handlers.AppHandler;
import org.example.userinterface.readers.Console;
import org.example.userinterface.textformatters.BoxPadder;
import org.example.userinterface.textformatters.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import static org.example.userinterface.textformatters.Theme.*;

public class UICommunity {

    private final static String HEADER_TITLE = "Communities";
    private final static String NO_COMMUNITIES = "No communities created.";
    private static final String FORMAT_COMMUNITY_SIMPLE = "r/%-20.20s | %s";
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
        String title = "r/" + c.getNickname();
        List<String> lines=new ArrayList<>();
        AppHandler app = AppHandler.getInstance();
        User user = app.getCurrentUser();

        lines.add("Created By: "+ c.getCommunityCreatorUsername());
        lines.add("Topic: "+ formatTopic(c.getTopic()));
        lines.add("");

        List<String> wrappedContent = TextWrapper.wrap(c.getDescription(),MAX_TEXT_WIDTH);
        lines.add("Description: ");
        lines.addAll(wrappedContent);
        lines.add("");
        lines.add("Member Count:");
        lines.add(String.valueOf(c.getMemberCount()));

        String boxedCommunity= BoxPadder.format(lines,title);
        console.info(boxedCommunity);
    }

    public void showCommunitySimple(Community c) {
        String NSFW = "No";
        if (c.isNSFW()) {
            NSFW = "Yes";
        }

        String comWithoutName = "Topic: " + formatTopic(c.getTopic()) + " | " + formatNSFW(NSFW);
        console.info(String.format(FORMAT_COMMUNITY_SIMPLE, c.getNickname(), comWithoutName));
    }
}
