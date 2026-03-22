package com.nimcet.quiz.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionDTO {
    @NotBlank private String questionText;
    @NotBlank private String optionA;
    @NotBlank private String optionB;
    @NotBlank private String optionC;
    @NotBlank private String optionD;
    @NotBlank @Pattern(regexp="[ABCD]") private String correctAnswer;
    private String manualSolution;
    @NotBlank private String subject;
    @NotBlank private String topic;
    @NotNull @Min(2008) @Max(2025) private Integer year;
    private String difficulty;
    private Integer expectedSolveTime;
}