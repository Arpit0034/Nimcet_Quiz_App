package com.nimcet.quiz.repository;

import com.nimcet.quiz.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByCreatedAtAfter(LocalDateTime dateTime);
}