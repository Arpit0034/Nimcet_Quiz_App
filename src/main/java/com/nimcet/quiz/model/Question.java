package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="questions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT", nullable=false)
    private String questionText;

    @Column(nullable=false) private String optionA;
    @Column(nullable=false) private String optionB;
    @Column(nullable=false) private String optionC;
    @Column(nullable=false) private String optionD;

    @Column(nullable=false, length=1)
    private String correctAnswer; // A/B/C/D

    @Column(columnDefinition="TEXT")
    private String manualSolution;

    @Column(nullable=false)
    private String subject; // Mathematics / Reasoning / Computer Awareness

    @Column(nullable=false)
    private String topic;

    @Column(nullable=false)
    private Integer year;

    private String difficulty; // Easy / Medium / Hard

    private Integer expectedSolveTime; // 10 / 30 / 60 / 90

    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}