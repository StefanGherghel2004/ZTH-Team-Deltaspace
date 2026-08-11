package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;


@Data
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Column(length = 1000, unique = false,nullable = false)
    private String content;

    @Column(name = "deleted", unique = false, nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "email", "id",
            "password", "dateOfBirth", "updatedAt"})
    private User user;


    // JPA entity inhertiance - table per class hierarchy InheritanceType.TABLE_PER_CLASS
    // todo AbstractTextEntity extended by Post and Comment
    // Comment can be simplified by always having only one private AbstractTextEntity parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "replies", "parentComment"})
    private Comment parentComment;

    @Column(nullable = false)
    private int upvotes = 0;

    @Column(nullable = false)
    private int downvotes = 0;

}
