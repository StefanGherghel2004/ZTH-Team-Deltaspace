package cli.backend.handlers;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;

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

        System.out.print("Please enter the community in which you would like to post " +
                "\n(or press Enter to post to u/" + user.getUsername() + "): ");
        String communityName = scan.nextLine();

        if (communityName.trim().isEmpty()) {
            communityName = "u/" + user.getUsername();
        }
        
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

        System.out.println("User: " + post.getUser().getUsername());
        System.out.println("Title: " + post.getPostTitle());
        if (post.getImageLink() != null) {

            System.out.println(post.getImageLink());
        }
        System.out.println(post.getPostContents() + "\n\n");
    }

    // dummy implementation
    public void viewFeed() {

        if (posts.isEmpty()) {
            System.out.println("No posts to show!");
            return;
        }

        List<Post> currentFeed = getRandomizedFeed(posts);

        for (Post post : currentFeed) {
            this.viewPost(post);
        }
    }

    public void showComments(Post post) {

        List<Comment> flatCommentsList = post.getComments();
        if (flatCommentsList == null || flatCommentsList.isEmpty()) {
            System.out.println("\n(No comments yet. Be the first to reply!)");
            return;
        }

            System.out.println("\n--- Discussion Thread ---");

        Map<Integer, List<Comment>> commentTree = new HashMap<>();
        for (Comment comment : flatCommentsList) {
            commentTree.putIfAbsent(comment.getIdParent(), new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        printThread(-1, commentTree, 0);
    }

    private void printThread(int parentId, Map<Integer, List<Comment>> commentTree, int depth) {

        List<Comment> replies = commentTree.get(parentId);

        if (replies != null) {
            for (Comment reply : replies) {

                String indent = "    ".repeat(depth);
                String branch = depth > 0 ? "|_ " : "";


                System.out.println(indent + branch + "[" + reply.getUsername() + "]: " + reply.getText() + "  (ID: " + reply.getId() + ")");

                printThread(reply.getId(), commentTree, depth + 1);
            }
        }
    }
    public List<Post> getPosts(){
        return this.posts;
    }
}
