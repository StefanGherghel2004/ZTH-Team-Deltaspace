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

    public void addPost (User user) {
        System.out.println("Welcome to the post creation page.");

        System.out.println("Please enter post title:");
        String postTitle = scan.nextLine();

        System.out.println("Please enter post contents:");
        String postContents = scan.nextLine();

        System.out.println("Please enter image link (or press Enter to skip):");
        String imageLink = scan.nextLine();

        if (imageLink.trim().isEmpty()) {
            imageLink = null;
        }

        posts.add(new Post(user, postTitle, postContents, imageLink));

        System.out.println("Post created successfully!");
    }

    public List<Post> getRandomizedFeed(List<Post> posts) {

        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(posts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }

    // dummy implementation
    public void viewFeed() {

        if (posts.isEmpty()) {
            System.out.println("No posts to show!");
            return;
        }

        List<Post> currentFeed = getRandomizedFeed(posts);

        for (Post post : currentFeed) {
            showPost(post);
        }

    }
}
