package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_reports")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long questionId;

    private Long userId;
    private String username;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private boolean resolved = false;

    private String resolvedAction; // "dismiss" or "deleted"

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}