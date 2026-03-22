package com.nimcet.quiz.controller;

import com.nimcet.quiz.model.QuestionReport;
import com.nimcet.quiz.repository.QuestionReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final QuestionReportRepository reportRepo;

    @PostMapping
    @Transactional
    public ResponseEntity<?> submitReport(
            @RequestBody Map<String, Object> req) {

        String username = (String) req.get("username");
        Long userId = req.get("userId") != null ? Long.valueOf(req.get("userId").toString()) : null;
        Long questionId = Long.valueOf(req.get("questionId").toString());
        String reason = (String) req.get("reason");
        String note = (String) req.get("note");

        QuestionReport report = QuestionReport.builder()
                .questionId(questionId)
                .userId(userId)
                .username(username)
                .reason(reason)
                .note(note)
                .resolved(false)
                .build();

        return ResponseEntity.ok(reportRepo.save(report));
    }
}
