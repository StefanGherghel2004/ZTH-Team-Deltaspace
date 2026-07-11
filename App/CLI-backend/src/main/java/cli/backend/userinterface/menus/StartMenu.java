package cli.backend.userinterface.menus;

import cli.backend.commands.startmenu.LoginCommand;
import cli.backend.commands.startmenu.QuitCommand;
import cli.backend.commands.startmenu.RegisterCommand;
import cli.backend.textformatters.Color;

public class StartMenu extends Menu{

    private static final String LOGO =
    Color.textBrightMagenta("██████╗ ███████╗██╗  ████████╗ █████╗ ███████╗██████╗  █████╗  ██████╗███████╗\n") +
    Color.textMagenta("██╔══██╗██╔════╝██║  ╚══██╔══╝██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔════╝\n") +
    Color.textBlue("██║  ██║█████╗  ██║     ██║   ███████║███████╗██████╔╝███████║██║     █████╗\n") +
    Color.textBrightBlue("██║  ██║██╔══╝  ██║     ██║   ██╔══██║╚════██║██╔═══╝ ██╔══██║██║     ██╔══╝\n") +
    Color.textCyan("██████╔╝███████╗███████╗██║   ██║  ██║███████║██║     ██║  ██║╚██████╗███████╗\n") +
    Color.textBrightCyan("╚═════╝ ╚══════╝╚══════╝╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝  ╚═╝ ╚═════╝╚══════╝");

    public StartMenu() {
        setTitle("Welcome to Deltaspace platform");
        addOption(1, "Register", new RegisterCommand());
        addOption(2, "Login", new LoginCommand());
        addOption(3, "Quit", new QuitCommand());
    }

    @Override
    public void showMenu(){
        System.out.println(LOGO);
        super.showMenu();
    }
}
