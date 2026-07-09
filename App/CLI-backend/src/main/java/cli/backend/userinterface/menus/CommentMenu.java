package cli.backend.userinterface.menus;

import cli.backend.Comment;

public class CommentMenu extends Menu {

    private Comment currentComment;

    public CommentMenu (Comment comment) {

        this.currentComment = comment;
    }

    @Override
    public void showMenu () {

        System.out.println("\n--- Selected Comment ---");
        System.out.println("[" + currentComment.getUsername() + "]: " + currentComment.getText());

        System.out.println("\n1. Reply");
        System.out.println("2. Back to Post");
    }
}
