package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Column(length = 1000, nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "email", "id",
            "password", "dateOfBirth", "updatedAt"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "replies", "parentComment"})
    private Comment parentComment;

    @Builder.Default
    @Column(nullable = false)
    private int upvotes = 0;

    @Builder.Default
    @Column(nullable = false)
    private int downvotes = 0;
}