package cli.backend.handlers;
import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;  // Import the Scanner class


public class CommentHandler {
    private static CommentHandler instance;

    private CommentHandler(){
    }
    public static CommentHandler getInstance(){
        if(instance==null){
            instance=new CommentHandler();
        }
        return instance;
    }
}
