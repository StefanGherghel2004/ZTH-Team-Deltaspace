package com.example.demo.repository;

import com.example.demo.model.Comment;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByParentCommentId(UUID parentId);

    List<Comment> findByPostIdAndParentCommentIdIsNull(UUID postId);

    //This is used just for the tldr bot
    Comment findByPostIdAndUserId(UUID userId, UUID postId);

    int countByPostId(UUID postId);

    UUID user(User user);
}
