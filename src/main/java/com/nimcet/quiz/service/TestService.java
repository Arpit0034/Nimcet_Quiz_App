package com.nimcet.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimcet.quiz.dto.TestResultDTO;
import com.nimcet.quiz.model.*;
import com.nimcet.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TestService {
    private final TestAttemptRepository attemptRepo;
    private final QuestionRepository questionRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public TestAttempt submit(TestResultDTO dto) throws Exception {
        List<Question> questions = questionRepo.findAllById(dto.getQuestionIds());
        Map<Long, String> answers = dto.getAnswers();

        int correct=0, incorrect=0, skipped=0;
        for (Question q : questions) {
            String ans = answers.get(q.getId());
            if (ans == null || ans.isEmpty()) skipped++;
            else if (ans.equals(q.getCorrectAnswer())) correct++;
            else incorrect++;
        }

        int score = correct * 3 - incorrect;

        TestAttempt attempt = TestAttempt.builder()
                .userId(dto.getUserId())
                .questionsJson(mapper.writeValueAsString(dto.getQuestionIds()))
                .answersJson(mapper.writeValueAsString(answers))
                .score(score).correct(correct).incorrect(incorrect).skipped(skipped)
                .timeTaken(dto.getTimeTaken())
                .subjectFilter(dto.getSubjectFilter())
                .topicFilter(dto.getTopicFilter())
                .yearFilter(dto.getYearFilter())
                .build();

        return attemptRepo.save(attempt);
    }

    public List<TestAttempt> getHistory(Long userId) {
        return attemptRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public TestAttempt getById(Long id) {
        return attemptRepo.findById(id).orElseThrow(() -> new RuntimeException("Test not found: " + id));
    }
}