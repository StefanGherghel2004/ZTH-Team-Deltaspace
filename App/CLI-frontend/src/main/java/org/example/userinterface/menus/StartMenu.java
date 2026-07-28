package org.example.userinterface.menus;


import org.example.commands.startmenu.LoginCommand;
import org.example.commands.startmenu.QuitCommand;
import org.example.commands.startmenu.RegisterCommand;
import org.example.userinterface.textformatters.Theme;

public class StartMenu extends Menu{

    public StartMenu() {
        setTitle("Welcome to Deltaspace platform");
        addOption("Register", new RegisterCommand());
        addOption("Login", new LoginCommand());
        addOption("Quit", new QuitCommand());
    }

    @Override
    public void showMenu(){
        System.out.print("\n" + Theme.LOGO);
        super.showMenu();
    }
}
