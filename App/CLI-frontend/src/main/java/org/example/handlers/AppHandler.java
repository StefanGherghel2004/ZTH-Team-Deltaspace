package org.example.handlers;

import lombok.Getter;
import lombok.Setter;
import org.example.Comment;
import org.example.Community;
import org.example.Post;
import org.example.User;
import org.example.commands.*;
import org.example.userinterface.menus.*;
import org.example.userinterface.readers.Console;

@Setter
@Getter
public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN,
        ON_COMMUNITY,
        EDIT_COMMUNITY,
        ON_POST,
        EDIT_POST,
        EDIT_USER,
        ON_COMMENT
    }

    private State currentState = State.NOT_LOGGED_IN;
    private User currentUser;
    private Community currentCommunity;
    private Post currentPost;
    private Comment currentComment;
    private String jwtToken;

    private static AppHandler instance;
    private static final Console console = Console.getInstance();

    private AppHandler() {

    }

    public static AppHandler getInstance() {
        if (instance == null) {
            instance = new AppHandler();
        }
        return instance;
    }

    public void run() {
        boolean isActive = true;
        while (isActive) {
            Menu currentMenu = getMenuForCurrentState();

            currentMenu.showMenu();
            int choice = console.getIntInRangeInput(1, currentMenu.getOptionsCount());
            Command command = currentMenu.getCommand(choice);
            if (command != null) {
                isActive = command.execute();
            }
        }
    }

    private Menu getMenuForCurrentState() {
        return switch (currentState) {
            case NOT_LOGGED_IN -> new StartMenu();
            case LOGGED_IN -> new MainMenu();
            case ON_COMMUNITY -> new CommunityMenu(currentCommunity);
            case EDIT_COMMUNITY -> new EditCommunityMenu(currentCommunity);
            case ON_POST -> new PostMenu(currentPost);
            case EDIT_POST -> new EditPostMenu(currentPost);
            case EDIT_USER -> new EditUserMenu(currentUser);
            case ON_COMMENT -> new CommentMenu(currentComment);
        };
    }

}