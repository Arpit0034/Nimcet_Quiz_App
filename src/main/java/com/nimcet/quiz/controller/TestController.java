package com.nimcet.quiz.controller;

import com.nimcet.quiz.dto.TestResultDTO;
import com.nimcet.quiz.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService service;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody TestResultDTO dto) {
        try { return ResponseEntity.ok(service.submit(dto)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(@RequestParam Long userId) {
        return ResponseEntity.ok(service.getHistory(userId));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getResult(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.getById(id)); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

}