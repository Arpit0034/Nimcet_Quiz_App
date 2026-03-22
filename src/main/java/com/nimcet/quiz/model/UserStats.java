package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="user_stats")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserStats {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false) private Long userId;
    private Integer streak = 0;
    private Long totalTimeSpent = 0L;

    @Column(columnDefinition="TEXT")
    private String accuracyHistory; // JSON array

    private LocalDateTime lastActive;

    @PrePersist @PreUpdate
    protected void onUpdate() { lastActive = LocalDateTime.now(); }
}