package cli.backend.userinterface.views;

import cli.backend.User;
import cli.backend.userinterface.readers.Console;
import cli.backend.userinterface.textformatters.BoxPadder;

import java.util.ArrayList;
import java.util.List;

public class UIUser {

    private static UIUser instance = null;
    private final Console console;

    private UIUser(){
        this.console = Console.getInstance();
    }

    public static UIUser getInstance() {
        if (instance == null) {
            instance = new UIUser();
        }
        return instance;
    }

    public void showUserProfile(User user) {

        String username = "u/" + user.getUsername();
        List<String> lines = new ArrayList<>();

        lines.add("Email: " + user.getEmail());
        lines.add("");
        lines.add("Date of birth: " + user.getDateOfBirth());

        String boxedUser = BoxPadder.format(lines,username);
        console.info(boxedUser);
    }
}
