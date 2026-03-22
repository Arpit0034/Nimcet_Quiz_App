package com.nimcet.quiz.dto;

import lombok.*;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TestResultDTO {
    private Long userId;
    private java.util.List<Long> questionIds;
    private Map<Long, String> answers;     // questionId -> A/B/C/D
    private Long timeTaken;
    private String subjectFilter;
    private String topicFilter;
    private String yearFilter;
}