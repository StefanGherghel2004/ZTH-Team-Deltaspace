package cli.backend.handlers;
import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.User;

import java.util.List;
import java.util.Scanner;  // Import the Scanner class


public class CommentHandler {


    private String text;

    public void addComment(User user, Post post ){
        Scanner sObject= new Scanner(System.in);
        System.out.println("Write Comment: ");
        text=sObject.nextLine();

        if(text.isEmpty()){
            System.out.println("Error! Please introduce your text");
        }

        else {
            Comment newComment = new Comment(text, user, post.getPostID());
        }
    }

    public void viewComment(Post post){
        List<Comment> lista=post.getComments();
        if(lista.isEmpty()){
            System.out.println("Be first to comment");
        }
        else {
            for(Comment comment:lista)
                System.out.println("#" + comment.getCommentPostId()+comment.getUsername() + comment.getText());
        }

    }
}
