package org.example.userinterface.menus;
import lombok.Setter;
import org.example.commands.Command;
import org.example.userinterface.readers.Console;
import org.example.userinterface.textformatters.BoxPadder;
import org.example.userinterface.textformatters.Capitalise;
import org.example.userinterface.textformatters.Theme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Menu {

    protected Map<Integer, MenuOption> options = new LinkedHashMap<>();
    @Setter
    protected String title = "";
    protected String BACK_COMMAND_INSTRUCTIONS = "You can always type ':back' on an empty line in order to escape a action.";

    protected void addOption(String description, Command command) {
        int nextKey = options.size() + 1;
        options.put(nextKey, new MenuOption(description, command));
    }

    public void showMenu() {
        List<String> formattedOptions = new ArrayList<>();

        for (Map.Entry<Integer, MenuOption> entry : options.entrySet()) {
            formattedOptions.add(Capitalise.format(entry.getKey() + ". " + entry.getValue().getDescription()));
        }
        String menuBox = BoxPadder.formatWithGradientBorder(
                formattedOptions,
                Capitalise.format(title),
                Theme.PRIMARY_GRADIENT_START,
                Theme.PRIMARY_GRADIENT_END
        );

        Console.getInstance().info("\n" + menuBox + BACK_COMMAND_INSTRUCTIONS);
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
