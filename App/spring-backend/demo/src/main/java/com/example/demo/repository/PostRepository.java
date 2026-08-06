package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE Post p SET p.upvotes = COALESCE(p.upvotes, 0) + 1 WHERE p.id = :postId")
    void incrementUpvotes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.upvotes = COALESCE(p.upvotes, 0) - 1 WHERE p.id = :postId")
    void decrementUpvotes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.downvotes = COALESCE(p.downvotes, 0) + 1 WHERE p.id = :postId")
    void incrementDownvotes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.downvotes = COALESCE(p.downvotes, 0) - 1 WHERE p.id = :postId")
    void decrementDownvotes(@Param("postId") UUID postId);
}