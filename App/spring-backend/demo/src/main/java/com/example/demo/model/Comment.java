package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Builder
@Data
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Column(length = 1000, unique = false,nullable = false)
    private String content;

    @Builder.Default
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

    @Builder.Default
    @Column(nullable = false)
    private int upvotes = 0;

    @Builder.Default
    @Column(nullable = false)
    private int downvotes = 0;

}
