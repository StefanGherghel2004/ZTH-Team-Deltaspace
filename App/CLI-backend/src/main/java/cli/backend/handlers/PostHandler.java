package cli.backend.handlers;

import cli.backend.Comment;
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

    public List<Post> getRandomizedFeed() {

        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(posts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }

    public void showAllPosts () {

        if (posts.isEmpty()) {
            System.out.println("No posts to show yet.");
            return;
        }

        System.out.println("--- Global Feed ---");
        for (Post post: posts) {
            System.out.println(String.valueOf(post.getPostID()) + " " + post.getUser() + " " + post.getPostTitle());
        }
    }

    public Post getPostByID(int id) {
        if (id >= 0 && id < posts.size()) {
            return posts.get(id);
        }
        System.out.println("Error: Post ID " + id + " does not exist.");
        return null;
    }

    public void showCommentsForPost(Post post) {

        if (post.getComments().isEmpty()) {
            System.out.println("There are currently no comments on this post.");
            return;
        }

        Map<Integer, List<Comment>> commentTree = new HashMap<>();
        for (Comment comment: post.getComments()) {
            commentTree.putIfAbsent(comment.getIdParent(),new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        System.out.println("--- Comments ---");
    }
}
