package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
// instead of deleting it will update the deleted field
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column
    private String profilePictureUrl;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

}