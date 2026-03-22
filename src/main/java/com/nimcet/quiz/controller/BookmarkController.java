package com.nimcet.quiz.controller;

import com.nimcet.quiz.model.Bookmark;
import com.nimcet.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkRepository bookmarkRepo;
    private final QuestionRepository questionRepo;

    @GetMapping
    public ResponseEntity<?> getBookmarks(@RequestParam Long userId) {
        List<Bookmark> bookmarks = bookmarkRepo.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            questionRepo.findById(b.getQuestionId()).ifPresent(q -> {
                result.add(Map.of("bookmark", b, "question", q));
            });
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> addBookmark(@RequestBody Map<String, Long> req) {
        Long userId = req.get("userId");
        Long questionId = req.get("questionId");
        if (bookmarkRepo.existsByUserIdAndQuestionId(userId, questionId))
            return ResponseEntity.badRequest().body(Map.of("message","Already bookmarked"));
        Bookmark b = Bookmark.builder().userId(userId).questionId(questionId).build();
        return ResponseEntity.ok(bookmarkRepo.save(b));
    }


    @DeleteMapping("/{questionId}")
    @Transactional
    public ResponseEntity<?> removeBookmark(@PathVariable Long questionId,
                                            @RequestParam Long userId) {
        bookmarkRepo.deleteByUserIdAndQuestionId(userId, questionId);
        return ResponseEntity.ok(Map.of("message","Bookmark removed"));
    }
}