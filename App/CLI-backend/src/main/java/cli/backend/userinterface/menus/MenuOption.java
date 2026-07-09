package cli.backend.userinterface.menus;

import cli.backend.commands.Command;

public class MenuOption {
    private String description;
    private Command command;

    public MenuOption(String description, Command command) {
        this.description = description;
        this.command = command;
    }

    public String getDescription() { return description; }
    public Command getCommand() { return command; }
}
