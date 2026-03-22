package com.nimcet.quiz.service;

import com.nimcet.quiz.dto.QuestionDTO;
import com.nimcet.quiz.model.Question;
import com.nimcet.quiz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question save(QuestionDTO dto) {
        Question q = Question.builder()
                .questionText(dto.getQuestionText())
                .optionA(dto.getOptionA()).optionB(dto.getOptionB())
                .optionC(dto.getOptionC()).optionD(dto.getOptionD())
                .correctAnswer(dto.getCorrectAnswer())
                .manualSolution(dto.getManualSolution())
                .subject(dto.getSubject()).topic(dto.getTopic())
                .year(dto.getYear()).difficulty(dto.getDifficulty())
                .expectedSolveTime(dto.getExpectedSolveTime())
                .build();
        return questionRepository.save(q);
    }

    public Question update(Long id, QuestionDTO dto) {
        Question q = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found: " + id));
        q.setQuestionText(dto.getQuestionText());
        q.setOptionA(dto.getOptionA()); q.setOptionB(dto.getOptionB());
        q.setOptionC(dto.getOptionC()); q.setOptionD(dto.getOptionD());
        q.setCorrectAnswer(dto.getCorrectAnswer());
        q.setManualSolution(dto.getManualSolution());
        q.setSubject(dto.getSubject()); q.setTopic(dto.getTopic());
        q.setYear(dto.getYear()); q.setDifficulty(dto.getDifficulty());
        q.setExpectedSolveTime(dto.getExpectedSolveTime());
        return questionRepository.save(q);
    }

    public void delete(Long id) { questionRepository.deleteById(id); }

    public Optional<Question> findById(Long id) { return questionRepository.findById(id); }

    public List<Question> findWithFilters(String subject, String topic, Integer year,
                                          String difficulty, Integer solveTime, Integer count, String mode) {
        List<Question> list = questionRepository.findWithFilters(
                "All".equals(subject)||subject==null?null:subject,
                "All".equals(topic)||topic==null?null:topic,
                year, difficulty,
                solveTime!=null&&solveTime==0?null:solveTime
        );
        if ("Random Shuffle".equals(mode)) Collections.shuffle(list);
        if (count != null && count > 0) list = list.stream().limit(count).collect(Collectors.toList());
        return list;
    }

    public List<String> getTopics(String subject) {
        return questionRepository.findDistinctTopicsBySubject(subject==null||"All".equals(subject)?null:subject);
    }

    public List<Integer> getYears() { return questionRepository.findDistinctYears(); }

    public List<Question> bulkSave(List<QuestionDTO> questionDTOS) {
        return questionDTOS.stream().map(this::save).collect(Collectors.toList());
    }
}