package com.example.demo.repository;

import com.example.demo.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SubredditRepository extends JpaRepository<Subreddit, UUID> {

    Optional<Subreddit> findByName(String name);

}