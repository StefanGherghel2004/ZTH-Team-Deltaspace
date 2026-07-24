package cli.backend.userinterface.menus;

import cli.backend.User;
import cli.backend.commands.BackCommand;
import cli.backend.commands.mainmenu.EditUserCommand;
import cli.backend.userinterface.views.UIUser;

public class EditUserMenu extends Menu {
    User currentUser;

    public EditUserMenu(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Edit user actions");
        addOption("Edit username", new EditUserCommand("username"));
        addOption("Edit email", new EditUserCommand("email"));
        addOption("Edit password", new EditUserCommand("password"));
        addOption("Edit date of birth", new EditUserCommand("dateOfBirth"));
        addOption("Back to post", new BackCommand());
    }

    @Override
    public void showMenu() {
        UIUser.getInstance().showUserProfile(currentUser);
        super.showMenu();
    }
}
