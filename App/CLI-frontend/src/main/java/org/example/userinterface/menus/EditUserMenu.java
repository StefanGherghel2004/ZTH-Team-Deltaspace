package org.example.userinterface.menus;

import org.example.User;
import org.example.commands.BackCommand;
import org.example.commands.mainmenu.EditUserCommand;
import org.example.commands.startmenu.DeleteUserCommand;
import org.example.userinterface.views.UIUser;

public class EditUserMenu extends Menu {
    User currentUser;

    public EditUserMenu(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Edit user actions");
        addOption("Edit username", new EditUserCommand("username"));
        addOption("Edit email", new EditUserCommand("email"));
        addOption("Edit password", new EditUserCommand("password"));
        addOption("Edit date of birth", new EditUserCommand("dateOfBirth"));
        addOption("Delete Account", new DeleteUserCommand());
        addOption("Back to main menu", new BackCommand());
    }

    @Override
    public void showMenu() {
        UIUser.getInstance().showUserProfile(currentUser);
        super.showMenu();
    }
}
