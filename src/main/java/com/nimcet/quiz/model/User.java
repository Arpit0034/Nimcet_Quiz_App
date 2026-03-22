package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false) private String username;
    @Column(unique=true, nullable=false) private String email;
    @Column(nullable=false) private String password;
    private String role; // STUDENT / ADMIN

    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if(role == null) role = "STUDENT";
    }
}