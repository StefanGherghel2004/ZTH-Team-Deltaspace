package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "posts",
indexes = {@Index(name = "idx_posts_community_id", columnList = "community_id, created_at DESC" )})
public class Post extends BaseEntity {

    @Column(length = 300, nullable = false)
    private String title;

    @Column(length=10000,columnDefinition = "TEXT")
    private String content;

    private String imageUrl;
    private boolean nsfw;

    private Integer filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // ignores fields added by hibernate
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "email", "id",
    "password", "dateOfBirth", "updatedAt"})
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(nullable = false)
    private int upvotes=0;

    @Column(nullable = false)
    private int downvotes=0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();
}