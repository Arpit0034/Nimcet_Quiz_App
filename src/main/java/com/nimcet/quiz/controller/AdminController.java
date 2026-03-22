package com.nimcet.quiz.controller;

import com.nimcet.quiz.config.JwtConfig;
import com.nimcet.quiz.dto.QuestionDTO;
import com.nimcet.quiz.model.QuestionReport;
import com.nimcet.quiz.repository.*;
import com.nimcet.quiz.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final QuestionService questionService;
    private final QuestionRepository questionRepo;
    private final UserRepository userRepo;
    private final TestAttemptRepository attemptRepo;
    private final JwtConfig jwtConfig;
    private final QuestionReportRepository reportRepo;
    private final BugReportRepository bugReportRepo;

    @Value("${admin.username}") private String adminUsername;
    @Value("${admin.password}") private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> req) {
        System.out.println("🔥🔥🔥 ADMIN LOGIN ENDPOINT CALLED 🔥🔥🔥");
        System.out.println("→ Received username: '" + req.get("username") + "'");
        System.out.println("→ Expected username: '" + adminUsername + "'");
        System.out.println("→ Received password: '" + req.get("password") + "'");
        System.out.println("→ Expected password: '" + adminPassword + "'");

        boolean match = adminUsername.equals(req.get("username")) &&
                adminPassword.equals(req.get("password"));
        System.out.println("→ MATCH RESULT: " + match);

        if (match) {
            System.out.println("✅✅✅ ADMIN LOGIN SUCCESS ✅✅✅");
            String token = jwtConfig.generateToken(adminUsername, "ADMIN");
            return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
        }
        System.out.println("❌❌❌ ADMIN LOGIN FAILED ❌❌❌");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message","Invalid admin credentials"));
    }

    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionService.save(dto));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionService.update(id, dto));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.ok(Map.of("message","Deleted successfully"));
    }

    @PostMapping("/questions/bulk")
    public ResponseEntity<?> bulkImport(@RequestBody List<QuestionDTO> dtos) {
        var saved = questionService.bulkSave(dtos);
        return ResponseEntity.ok(Map.of("imported", saved.size(), "total", dtos.size()));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalQ = questionRepo.count();
        long totalStudents = userRepo.count();
        long testsToday = attemptRepo.countByCreatedAtAfter(LocalDateTime.now().toLocalDate().atStartOfDay());
        Map<String, Long> subjectCount = Map.of(
                "Mathematics", questionRepo.countBySubject("Mathematics"),
                "Reasoning", questionRepo.countBySubject("Reasoning"),
                "Computer Awareness", questionRepo.countBySubject("Computer Awareness")
        );
        return ResponseEntity.ok(Map.of(
                "totalQuestions", totalQ,
                "totalStudents", totalStudents,
                "testsToday", testsToday,
                "subjectCount", subjectCount
        ));
    }

    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        return ResponseEntity.ok(userRepo.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .map(u -> Map.of("id", u.getId(), "username", u.getUsername(),
                        "email", u.getEmail(), "createdAt", u.getCreatedAt()))
                .toList());
    }

    @Value("${gemini.api.key}") private String geminiKey;
    @Value("${gemini.api.url}") private String geminiUrl;
    @PostMapping("/extract-pdf")
    public ResponseEntity<?> extractPdf(@RequestBody Map<String, String> req) {
        try {
            String base64 = req.get("base64");
            String mimeType = req.get("mimeType");
            String prompt = req.get("prompt");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> part1 = Map.of("inline_data", Map.of("mime_type", mimeType, "data", base64));
            Map<String, Object> part2 = Map.of("text", prompt);
            Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(part1, part2))));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<Map> response = rt.postForEntity(geminiUrl + "?key=" + geminiKey, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            return ResponseEntity.ok(Map.of("text", text));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReports() {
        return ResponseEntity.ok(reportRepo.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> req) {
        QuestionReport report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setResolved(true);
        report.setResolvedAction(req.get("action"));
        report.setResolvedAt(java.time.LocalDateTime.now());
        reportRepo.save(report);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/bug-reports")
    public ResponseEntity<?> getBugReports() {
        return ResponseEntity.ok(bugReportRepo.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping("/bug-reports/{id}/resolve")
    public ResponseEntity<?> resolveBugReport(@PathVariable Long id) {
        return bugReportRepo.findById(id).map(r -> {
            r.setResolved(true);
            r.setResolvedAt(java.time.LocalDateTime.now());
            bugReportRepo.save(r);
            return ResponseEntity.ok(Map.of("message", "Resolved"));
        }).orElse(ResponseEntity.notFound().build());
    }
}