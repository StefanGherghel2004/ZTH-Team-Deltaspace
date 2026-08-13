package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
@SuperBuilder
@Entity

@Table(name = "communities")
public class Subreddit extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name="display_name",unique = false, nullable = false)
    private String displayName;

    @Column(nullable = true)
    private String topic;

    @Column(name="icon_url")
    private String iconUrl;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // CascadeType.ALL ensures community deletion also removes all associated posts.
    @OneToMany(mappedBy = "subreddit", cascade = CascadeType.ALL)
    @JsonIgnore

    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "community_members",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    @Singular
    @JsonIgnore
    private Set<User> members = new HashSet<>();

}