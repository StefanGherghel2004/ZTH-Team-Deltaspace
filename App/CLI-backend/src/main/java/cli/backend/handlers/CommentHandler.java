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

            post.addComment(newComment, -1);
            System.out.println("Comment added successfully!");
        }
    }

    public void replyToComment(User user, Post post, Comment parentComment, String text) {

        if (text == null || text.trim().isEmpty()) {
            System.out.println("Error! Comment text cannot be empty.");
            return;
        }

        Comment reply = new Comment(text, user, post.getPostID());

        reply.setIdParent(parentComment.getId());
        post.addComment(reply, parentComment.getId());

        System.out.println("Reply added successfully!");
    }

    // maybe this is not needed now
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
