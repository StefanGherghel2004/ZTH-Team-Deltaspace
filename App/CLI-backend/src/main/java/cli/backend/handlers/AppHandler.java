package cli.backend.handlers;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.commands.Command;
import cli.backend.userinterface.readers.ConsoleReader;
import cli.backend.userinterface.menus.*;

public class AppHandler {

    public enum State {
        NOT_LOGGED_IN,
        LOGGED_IN,
        ON_COMMUNITY,
        ON_POST,
        ON_COMMENT
    }

    private State currentState = State.NOT_LOGGED_IN;
    private User currentUser;
    private Community currentCommunity;
    private Post currentPost;
    private Comment currentComment;

    private static AppHandler instance;
    private static ConsoleReader consoleReader = ConsoleReader.getInstance();

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
            int choice = consoleReader.readIntInRange(1, currentMenu.getOptionsCount());
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
            case ON_POST -> new PostMenu(currentPost);
            case ON_COMMENT -> new CommentMenu(currentComment);
        };
    }

    public State getCurrentState() {
        return currentState;
    }
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Community getCurrentCommunity() {
        return currentCommunity;
    }

    public void setCurrentCommunity(Community currentCommunity) {
        this.currentCommunity = currentCommunity;
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

    public Comment getCurrentComment() {
        return currentComment;
    }

    public void setCurrentComment(Comment currentComment) {
        this.currentComment = currentComment;
    }
}