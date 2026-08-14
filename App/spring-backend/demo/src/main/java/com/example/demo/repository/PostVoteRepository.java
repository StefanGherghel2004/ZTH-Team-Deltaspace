package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.PostVote;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, UUID> {

    Optional<PostVote> findByPostAndUser(Post post, User user);

    List<PostVote> findByUser(User currentUser);
}