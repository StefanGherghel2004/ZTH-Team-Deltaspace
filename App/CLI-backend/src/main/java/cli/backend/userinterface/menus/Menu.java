package cli.backend.userinterface.menus;

import cli.backend.commands.Command;
import cli.backend.readers.Console;
import cli.backend.textformatters.BoxPadder;
import cli.backend.textformatters.Theme;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Menu {

    protected Map<Integer, MenuOption> options = new LinkedHashMap<>();
    @Setter
    protected String title = "";

    protected void addOption(String description, Command command) {
        int nextKey = options.size() + 1;
        options.put(nextKey, new MenuOption(description, command));
    }

    public void showMenu() {
        List<String> formattedOptions = new ArrayList<>();

        for (Map.Entry<Integer, MenuOption> entry : options.entrySet()) {
            formattedOptions.add(entry.getKey() + ". " + entry.getValue().getDescription());
        }

        String menuBox = BoxPadder.formatWithGradientBorder(
                formattedOptions,
                title,
                Theme.PRIMARY_GRADIENT_START,
                Theme.PRIMARY_GRADIENT_END
        );

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
