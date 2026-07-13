package cli.backend.services;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.exceptions.EmptyCommentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {

    private static CommentService instance;

    private CommentService() {

    }

    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    public void addComment (User user, Post post, String text) throws EmptyCommentException {

        if(text.isEmpty()){
            throw new EmptyCommentException("Error! Please introduce your text");
        } else {
            Comment newComment = new Comment(text, user, post.getPostID());

            post.addComment(newComment, -1);
        }
    }

    public Map<Integer, List<Comment>> getCommentTree(Post post) {
        Map<Integer, List<Comment>> commentTree = new HashMap<>();
        List<Comment> flatCommentsList = post.getComments();

        if (flatCommentsList == null || flatCommentsList.isEmpty()) {
            return commentTree;
        }

        for (Comment comment : flatCommentsList) {
            commentTree.putIfAbsent(comment.getIdParent(), new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        return commentTree;
    }

    public void replyToComment (User user, Post post, Comment parentComment, String text)
            throws EmptyCommentException {

        if (text == null || text.trim().isEmpty())
            throw new EmptyCommentException("Error! Comment text cannot be empty.");

        Comment reply = new Comment(text, user, post.getPostID());

        reply.setIdParent(parentComment.getId());
        post.addComment(reply, parentComment.getId());
    }

    public boolean deleteComment(Post post, Comment comment) {
        if (post == null || comment == null) {
            return false;
        }
        return post.getComments().removeIf(c -> c.equals(comment));
    }

    public boolean canUserDeleteComment(User user, Comment comment, Community community) {
        if (user == null || comment == null) {
            return false;
        }

        String username = user.getUsername();

        if ("admin".equals(username)) {
            return true;
        }

        if (comment.getUsername().equals(username)) {
            return true;
        }

        if (community != null && community.getCommunityCreator().equals(username)) {
            return true;
        }

        return false;
    }

}
