package cli.backend.userinterface.menus;

import cli.backend.commands.Command;
import cli.backend.readers.Console;
import cli.backend.textformatters.BoxPadder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Menu {

    protected Map<Integer, MenuOption> options = new LinkedHashMap<>();
    protected String title = "";

    public void setTitle(String title) {
        this.title = title;
    }

    protected void addOption(int key, String description, Command command) {
        options.put(key, new MenuOption(description, command));
    }

    public void showMenu() {
        List<String> formattedOptions = new ArrayList<>();

        for (Map.Entry<Integer, MenuOption> entry : options.entrySet()) {
            formattedOptions.add(entry.getKey() + ". " + entry.getValue().getDescription());
        }

        String menuBox = BoxPadder.format(formattedOptions, title);
        Console.getInstance().info(menuBox);
    }

    public Command getCommand(int choice) {
        if (options.containsKey(choice)) {
            return options.get(choice).getCommand();
        }
        return null;
    }

    public int getOptionsCount() {
        return options.size();
    }
}
