package cli.backend.userinterface.menus;

import cli.backend.commands.startmenu.LoginCommand;
import cli.backend.commands.startmenu.QuitCommand;
import cli.backend.commands.startmenu.RegisterCommand;
import cli.backend.textformatters.Theme;


public class StartMenu extends Menu{

    public StartMenu() {
        setTitle("Welcome to Deltaspace platform");
        addOption("Register", new RegisterCommand());
        addOption("Login", new LoginCommand());
        addOption("Quit", new QuitCommand());
    }

    @Override
    public void showMenu(){
        System.out.println("\n" + Theme.LOGO);
        super.showMenu();
    }
}
