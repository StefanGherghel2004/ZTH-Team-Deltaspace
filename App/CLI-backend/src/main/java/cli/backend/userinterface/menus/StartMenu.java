package cli.backend.userinterface.menus;

import cli.backend.commands.startmenu.LoginCommand;
import cli.backend.commands.startmenu.QuitCommand;
import cli.backend.commands.startmenu.RegisterCommand;
import cli.backend.textformatters.Theme;


public class StartMenu extends Menu{

    public StartMenu() {
        setTitle("Welcome to Deltaspace platform");
        addOption(1, "Register", new RegisterCommand());
        addOption(2, "Login", new LoginCommand());
        addOption(3, "Quit", new QuitCommand());
    }

    @Override
    public void showMenu(){
        System.out.println(Theme.LOGO);
        super.showMenu();
    }
}
