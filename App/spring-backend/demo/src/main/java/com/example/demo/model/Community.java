package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@Data
@Entity
@Table(name = "communities")
public class Community extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String topic;

    @Column(name = "nsfw", nullable = true)
    private Boolean NSFW=false;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // CascadeType.ALL ensures community deletion also removes all associated posts.
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

}