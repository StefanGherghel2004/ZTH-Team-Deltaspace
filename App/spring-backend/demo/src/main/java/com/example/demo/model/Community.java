package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(name = "communities")
public class Community extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name="display_name",unique = false, nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String topic;

    @Column(name = "nsfw", nullable = true)
    private Boolean NSFW=false;

    @Column(name="icon_url")
    private String iconUrl;
    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // CascadeType.ALL ensures community deletion also removes all associated posts.
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "community_members",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> members = new HashSet<>();

}