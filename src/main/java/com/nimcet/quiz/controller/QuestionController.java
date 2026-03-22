package com.nimcet.quiz.controller;

import com.nimcet.quiz.model.Question;
import com.nimcet.quiz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService service;

    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(
            @RequestParam(required=false) String subject,
            @RequestParam(required=false) String topic,
            @RequestParam(required=false) Integer year,
            @RequestParam(required=false) String difficulty,
            @RequestParam(required=false) Integer expectedSolveTime,
            @RequestParam(required=false) Integer count,
            @RequestParam(required=false) String mode) {
        return ResponseEntity.ok(service.findWithFilters(subject, topic, year, difficulty, expectedSolveTime, count, mode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/topics")
    public ResponseEntity<?> getTopics(@RequestParam(required=false) String subject) {
        return ResponseEntity.ok(service.getTopics(subject));
    }

    @GetMapping("/years")
    public ResponseEntity<?> getYears() {
        return ResponseEntity.ok(service.getYears());
    }
}