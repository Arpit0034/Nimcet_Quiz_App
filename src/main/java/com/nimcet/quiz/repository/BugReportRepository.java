package com.nimcet.quiz.repository;


import com.nimcet.quiz.model.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BugReportRepository extends JpaRepository<BugReport, Long> {
    List<BugReport> findAllByOrderByCreatedAtDesc();
    List<BugReport> findByResolvedFalseOrderByCreatedAtDesc();
}
