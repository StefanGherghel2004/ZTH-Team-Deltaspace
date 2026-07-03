package cli.backend.handlers;

import cli.backend.Post;
import cli.backend.User;

import java.util.*;

public class PostHandler {

    private User user;
    private List<Post> posts;
    private static PostHandler instance;

    Scanner scan = new Scanner(System.in);

    private PostHandler () {

        this.posts = new java.util.ArrayList<>();
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
    }

    public void addPost (User user, String postTitle, String postContents, String imageLink) {

        posts.add(new Post(user,postTitle,postContents,imageLink));
    }

    public List<Post> getRandomizedFeed(List<Post> posts) {

        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(posts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }
}
