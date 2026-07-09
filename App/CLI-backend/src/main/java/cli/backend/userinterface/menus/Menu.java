package cli.backend.userinterface.menus;

import cli.backend.commands.Command;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Menu {

    protected Map<Integer, MenuOption> options = new LinkedHashMap<>();

    protected void addOption(int key, String description, Command command) {
        options.put(key, new MenuOption(description, command));
    }

    public void showMenu() {
        for (Map.Entry<Integer, MenuOption> entry : options.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue().getDescription());
        }
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
