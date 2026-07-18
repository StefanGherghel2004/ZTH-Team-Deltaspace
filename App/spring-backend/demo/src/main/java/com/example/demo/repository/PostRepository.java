package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunityName(String name);
    boolean existsByCommunityNameAndNsfwTrue(String name);
    Optional<Post> findPostById(Long id);
    @Query (value ="SELECT * FROM posts ORDER BY MD5(CONCAT(id,:seed)) LIMIT :limit OFFSET :offset",nativeQuery=true)
    List<Post> getRandomizedFeed (@Param("seed") String seed, @Param("limit") int limit, @Param("offset") int offset);
}