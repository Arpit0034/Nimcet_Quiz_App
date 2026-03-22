package com.nimcet.quiz.controller;

import com.nimcet.quiz.model.BugReport;
import com.nimcet.quiz.repository.BugReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bug-reports")
@RequiredArgsConstructor
public class BugReportController {

    private final BugReportRepository bugReportRepo;

    @PostMapping
    public ResponseEntity<?> submitBugReport(@RequestBody Map<String, Object> req) {
        String description = (String) req.get("description");
        if (description == null || description.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Description is required"));
        }
        BugReport report = new BugReport();
        report.setUserId(req.get("userId") != null ? Long.valueOf(req.get("userId").toString()) : null);
        report.setUsername((String) req.get("username"));
        report.setDescription(description);
        report.setCategory((String) req.getOrDefault("category", "Other"));
        report.setPageUrl((String) req.get("pageUrl"));
        bugReportRepo.save(report);
        return ResponseEntity.ok(Map.of("message", "Bug report submitted"));
    }
}
