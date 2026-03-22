package com.nimcet.quiz.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bug_reports")
@Data
public class BugReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // "UI Bug", "Wrong Data", "App Crash", "Other"
    private String pageUrl;  // which page they were on

    private boolean resolved = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;
}
