package cli.backend.userinterface.menus;

import cli.backend.Comment;
import cli.backend.commands.BackCommand;
import cli.backend.commands.postmenu.ReplyToCommentCommand;

public class CommentMenu extends Menu {

    private Comment currentComment;

    public CommentMenu(Comment comment) {
        this.currentComment = comment;

        addOption(1, "Reply", new ReplyToCommentCommand());
        addOption(2, "Back to Post", new BackCommand());
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());
        super.showMenu();
    }
}
