package cli.backend.userinterface.menus;

import cli.backend.Post;

public class PostMenu extends Menu {

    Post currentPost;

    public PostMenu(Post currentPost) {

        this.currentPost = currentPost;
    }

    @Override
    public void showMenu () {

        System.out.println("1. Show comments");
        System.out.println("2. Add comment");
        System.out.println("3. Select comment (Reply)");
        if(currentPost.getCommunityName().equalsIgnoreCase("u/" + currentPost.getUser().getUsername())) {
            System.out.println("4. Back to Main Menu");
        }
        else {
            System.out.println("4. Back to Community");
        }
    }
}
