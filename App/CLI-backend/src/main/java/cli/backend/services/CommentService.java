package cli.backend.services;

import cli.backend.Comment;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.exceptions.EmptyCommentException;

import java.util.List;

public class CommentService {

    public static void addComment (User user, Post post, String text) throws EmptyCommentException {

        if(text.isEmpty()){
            throw new EmptyCommentException("Error! Please introduce your text");
        } else {
            Comment newComment = new Comment(text, user, post.getPostID());

            post.addComment(newComment, -1);
        }
    }

    public static boolean replyToComment (User user, Post post, Comment parentComment, String text)
            throws EmptyCommentException {

        if (text == null || text.trim().isEmpty())
            throw new EmptyCommentException("Error! Comment text cannot be empty.");

        Comment reply = new Comment(text, user, post.getPostID());

        reply.setIdParent(parentComment.getId());
        post.addComment(reply, parentComment.getId());

        return true;
    }

    public static boolean viewComments (Post post) {

        List<Comment> comments=post.getComments();
        if(comments.isEmpty())
            return false;
        else {
            for(Comment comment:comments)
                System.out.println("#" + comment.getCommentPostId()+comment.getUsername()
                        + comment.getText());
            return true;
        }
    }
}
