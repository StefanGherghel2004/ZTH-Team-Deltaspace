package com.example.demo.repository;

import com.example.demo.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID> {

    Optional<Community> findByName(String name);

}