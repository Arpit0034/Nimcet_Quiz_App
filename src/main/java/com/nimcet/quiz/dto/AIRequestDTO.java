package com.nimcet.quiz.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AIRequestDTO {
    private Long questionId;
    private String type; // explain / hint / similar
}