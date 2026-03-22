package com.nimcet.quiz.repository;

import com.nimcet.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(String subject);
    List<Question> findBySubjectAndTopic(String subject, String topic);
    List<Question> findByYear(Integer year);
    List<Question> findByTopic(String topic);
    List<Question> findByExpectedSolveTime(Integer time);
    List<Question> findByDifficulty(String difficulty);

    @Query("SELECT DISTINCT q.topic FROM Question q WHERE (:subject IS NULL OR q.subject = :subject)")
    List<String> findDistinctTopicsBySubject(@Param("subject") String subject);

    @Query("SELECT DISTINCT q.year FROM Question q ORDER BY q.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT q FROM Question q WHERE " +
            "(:subject IS NULL OR q.subject = :subject) AND " +
            "(:topic IS NULL OR q.topic = :topic) AND " +
            "(:year IS NULL OR q.year = :year) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "(:solveTime IS NULL OR q.expectedSolveTime = :solveTime)")
    List<Question> findWithFilters(@Param("subject") String subject,
                                   @Param("topic") String topic,
                                   @Param("year") Integer year,
                                   @Param("difficulty") String difficulty,
                                   @Param("solveTime") Integer solveTime);

    long countBySubject(String subject);
}