package com.nimcet.quiz.repository;

import com.nimcet.quiz.model.QuestionReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionReportRepository extends JpaRepository<QuestionReport, Long> {
    List<QuestionReport> findAllByOrderByCreatedAtDesc();
    List<QuestionReport> findByResolvedFalseOrderByCreatedAtDesc();
    boolean existsByQuestionIdAndUserIdAndResolvedFalse(Long questionId, Long userId);
}
