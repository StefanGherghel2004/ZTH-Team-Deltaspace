package cli.backend.handlers;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;

import java.util.*;

public class PostHandler {

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

    public Post addPost (User user, Community currentCommunity) {
        System.out.println("Welcome to the post creation page.");

        Community targetCommunity = currentCommunity;

        if (targetCommunity == null) {
            System.out.print("Please enter the community in which you would like to post " +
                    "\n(or press Enter to post to u/" + user.getUsername() + "): r/");
            String communityName = scan.nextLine().trim();

            if (!communityName.isEmpty()) {
                //targetCommunity = CommunityHandler.getInstance().findCommunityByName("r/" + communityName);
                if (targetCommunity == null) {
                    System.out.println("Community not found! Posting to your profile instead.");
                }
            }
        }

        String targetName = (targetCommunity != null) ? targetCommunity.getNickname() : "u/" + user.getUsername();
        
        System.out.println("Please enter post title:");
        String postTitle = scan.nextLine();

        System.out.println("Please enter post contents:");
        String postContents = scan.nextLine();

        System.out.println("Please enter image link (or press Enter to skip):");
        String imageLink = scan.nextLine();

        if (imageLink.trim().isEmpty()) {
            imageLink = null;
        }

        Post newPost = new Post(user, postTitle, postContents, imageLink, targetName);

        posts.add(newPost);

        if (targetCommunity != null) {
            targetCommunity.addPost(newPost);
        }

        System.out.println("Post created successfully!");

        return newPost;
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

        System.out.println("ID: " + post.getPostID());
        System.out.println("Community: " + post.getCommunityName());
        System.out.println("Author: " + post.getUser().getUsername());
        System.out.println("Title: " + post.getPostTitle());
        if (post.getImageLink() != null) {

            System.out.println("Image: " + post.getImageLink());
        }
        System.out.println("Content: " + post.getPostContents());
        System.out.println("Comments counter: " + post.getCommentsCount() + "\n");
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


    public Post findPostById(int id) {
        for (Post post : posts) {
            if (post.getPostID() == id) {
                return post;
            }
        }
        return null;
    }
}
