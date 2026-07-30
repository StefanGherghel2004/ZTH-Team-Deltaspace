package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
public class CommentVote extends BaseVote{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;
}
