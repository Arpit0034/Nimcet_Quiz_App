package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="test_attempts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TestAttempt {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private Long userId;

    @Column(columnDefinition="TEXT")
    private String questionsJson; // JSON array of question IDs

    @Column(columnDefinition="TEXT")
    private String answersJson;   // JSON map: questionId -> answer

    private Integer score;
    private Integer correct;
    private Integer incorrect;
    private Integer skipped;
    private Long timeTaken; // seconds

    private String subjectFilter;
    private String topicFilter;
    private String yearFilter;

    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}