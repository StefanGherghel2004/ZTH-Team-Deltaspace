package com.example.demo.repository;

import com.example.demo.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Optional<Community> findByName(String name);

}