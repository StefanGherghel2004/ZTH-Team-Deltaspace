package cli.backend.handlers;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;

import java.sql.SQLOutput;
import java.util.*;

public class PostHandler {

    private User user;
    private List<Post> posts;
    private static PostHandler instance;
    private Community postCommunity;

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

    public void addPost (User user) {
        System.out.println("Welcome to the post creation page.");

        System.out.print("Please enter the community in which you would like to post:");
        String communityName = scan.nextLine();

        System.out.println("Please enter post title:");
        String postTitle = scan.nextLine();

        System.out.println("Please enter post contents:");
        String postContents = scan.nextLine();

        System.out.println("Please enter image link (or press Enter to skip):");
        String imageLink = scan.nextLine();

        if (imageLink.trim().isEmpty()) {
            imageLink = null;
        }

        posts.add(new Post(user, postTitle, postContents, imageLink, communityName));

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

    public void viewPost (Post post) {

        System.out.println("User: " + post.getUser());
        System.out.println("Title: " + post.getPostTitle());
        if (!post.getImageLink().isEmpty()) {

            System.out.println(post.getImageLink());
        }
        System.out.println(post.getPostContents());
    }

    // dummy implementation
    public void viewFeed() {

        if (posts.isEmpty()) {
            System.out.println("No posts to show!");
            return;
        }

        List<Post> currentFeed = getRandomizedFeed(posts);

        for (Post post : currentFeed) {
            System.out.println(post);
        }
    }

    public void showComments () {


    }
}
