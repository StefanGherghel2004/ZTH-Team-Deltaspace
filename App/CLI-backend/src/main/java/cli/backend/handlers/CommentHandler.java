package cli.backend.handlers;
import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;  // Import the Scanner class


public class CommentHandler {
    private static CommentHandler instance;
    private String text;

    private CommentHandler(){
    }
    public static CommentHandler getInstance(){
        if(instance==null){
            instance=new CommentHandler();
        }
        return instance;
    }



    public void addComment(User user, Post post ){
        Scanner sObject= new Scanner(System.in);
        System.out.println("Write Comment: ");
        text=sObject.nextLine();

        if(text.isEmpty()){
            System.out.println("Error! Please introduce your text");
        } else {
            Comment newComment = new Comment(text, user, post.getPostID());

            post.getComments().add(newComment);
            System.out.println("Comment added successfully!");
        }
    }

    public void replyToComment(User user, Post post) {

        if (post.getComments().isEmpty()) {
            System.out.println("There are no comments to reply to.");
            return;
        }

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the ID of the comment you want to reply to: ");

        int parentId;
        try {
            parentId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        Comment parentComment = findCommentById(post, parentId);

        if (parentComment == null) {
            System.out.println("Comment not found.");
            return;
        }

        System.out.print("Write reply: ");
        String text = sc.nextLine().trim();

        Comment reply = new Comment(text, user, post.getPostID());
        reply.setIdParent(parentComment.getId());

        post.getComments().add(reply);

        System.out.println("Reply added successfully!");
    }

    private Comment findCommentById(Post post, int id) {

        for (Comment comment : post.getComments()) {
            if (comment.getId() == id) {
                return comment;
            }
        }

        return null;
    }



    public void viewComment(Post post){
        List<Comment> comments=post.getComments();
        if(comments.isEmpty()){
            System.out.println("Be first to comment");
        }
        else {
            for(Comment comment:comments)
                System.out.println("#" + comment.getCommentPostId()+comment.getUsername() + comment.getText());
        }

    }
}
