package cli.backend.services;

import cli.backend.Comment;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.repositories.CommentRepository;
import cli.backend.exceptions.EmptyCommentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {

    private static CommentService instance;
    private static CommentRepository commentRepository = CommentRepository.getInstance();

    private CommentService() {

    }

    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    public void addComment (String authorUsername, Post post, String text) throws EmptyCommentException {

        if(text.isEmpty()){
            throw new EmptyCommentException("Error! Please introduce your text");
        } else {
            Comment newComment = new Comment(text, authorUsername, post.getId());
            commentRepository.addComment(newComment);
        }
    }

    public Map<Long, List<Comment>> getCommentTree(Post post) {
        Map<Long, List<Comment>> commentTree = new HashMap<>();
        List<Comment> flatCommentsList = commentRepository.findCommentsByPostId(post.getId());

        if (flatCommentsList == null || flatCommentsList.isEmpty()) {
            return commentTree;
        }

        for (Comment comment : flatCommentsList) {
            commentTree.putIfAbsent(comment.getIdParent(), new ArrayList<>());
            commentTree.get(comment.getIdParent()).add(comment);
        }

        return commentTree;
    }

    public void replyToComment (String authorUsername, Post post, Comment parentComment, String text)
            throws EmptyCommentException {

        if (text == null || text.trim().isEmpty())
            throw new EmptyCommentException("Error! Comment text cannot be empty.");

        Comment reply = new Comment(text, authorUsername, post.getId());

        reply.setIdParent(parentComment.getId());
        commentRepository.addComment(reply);
    }

    public boolean deleteComment(Comment comment) {
        if (comment == null) {
            return false;
        }
        commentRepository.deleteCommentById(comment.getId());
        return true;
    }

    public boolean canUserDeleteComment(User user, Comment comment, Community community) {
        if (user == null || comment == null) {
            return false;
        }

        String username = user.getUsername();

        if ("admin".equals(username)) {
            return true;
        }

        if (comment.getAuthorUsername().equals(username)) {
            return true;
        }

        if (community != null && community.getCommunityCreator().equals(username)) {
            return true;
        }

        return false;
    }

}
