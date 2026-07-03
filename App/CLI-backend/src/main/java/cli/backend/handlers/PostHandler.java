package cli.backend.handlers;

import cli.backend.Post;
import cli.backend.User;

import java.util.List;
import java.util.Scanner;

public class PostHandler {

    private User user;
    private List<Post> post;
    private static PostHandler instance;

    Scanner scan = new Scanner(System.in);

    private PostHandler () {

    }

    public static PostHandler getInstance () {

        if (instance == null) {
            instance = new PostHandler();
        }

        return instance;
    }

    public void showPost (Post post) {

        System.out.println("User: " + post.getUser().getUsername());
        System.out.println("Title: " + post.getPostTitle() + "\n");
        System.out.println(post.getPostContents());

        if (post.getImageLink() != null) {
            System.out.println("Image link: " + post.getImageLink());
        }

        System.out.println("1. Show comments");
    }

}
