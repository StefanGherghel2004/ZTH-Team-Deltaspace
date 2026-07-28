package org.example.util;

import org.example.Community;
import org.example.Post;
import org.example.User;

public class AuthorizationService {

    public static boolean canUserEditPost(User currentUser, Post post) {
        if (currentUser == null || post == null) return false;

        return currentUser.getUsername().equals(post.getAuthor());
    }

    public static boolean canUserDeletePost(User currentUser, Post post, Community community) {
        if (currentUser == null || post == null) return false;

        boolean isAuthor = currentUser.getUsername().equals(post.getAuthor());
        // de adaugat alta logica

        return isAuthor;
    }
}