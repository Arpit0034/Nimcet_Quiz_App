package com.nimcet.quiz.controller;

import com.nimcet.quiz.dto.AIRequestDTO;
import com.nimcet.quiz.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @PostMapping("/explain")
    public ResponseEntity<?> explain(@RequestBody AIRequestDTO req) {
        String result = aiService.explainQuestion(req.getQuestionId());
        return ResponseEntity.ok(Map.of("explanation", result));
    }

    @PostMapping("/hint")
    public ResponseEntity<?> hint(@RequestBody AIRequestDTO req) {
        String result = aiService.getHint(req.getQuestionId());
        return ResponseEntity.ok(Map.of("hint", result));
    }

    @PostMapping("/similar")
    public ResponseEntity<?> similar(@RequestBody AIRequestDTO req) {
        String result = aiService.generateSimilar(req.getQuestionId());
        return ResponseEntity.ok(Map.of("question", result));
    }
}