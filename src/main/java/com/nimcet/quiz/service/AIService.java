package com.nimcet.quiz.service;

import com.nimcet.quiz.model.Question;
import com.nimcet.quiz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AIService {
    private final QuestionRepository questionRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${groq.api.key:}") private String groqApiKey;
    @Value("${groq.api.url}") private String groqApiUrl;
    @Value("${groq.model}") private String groqModel;

    private String callGroq(String prompt) {
        if (groqApiKey == null || groqApiKey.isBlank()) {
            return "AI features unavailable: GROQ_API_KEY not configured.";
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            Map<String, Object> message = Map.of("role", "user", "content", prompt);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", groqModel);
            body.put("messages", List.of(message));
            body.put("max_tokens", 1024);
            body.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(groqApiUrl, entity, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
            return (String) msg.get("content");
        } catch (Exception e) {
            return "AI Error: " + e.getMessage();
        }
    }

    public String explainQuestion(Long questionId) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        String prompt = String.format(
                "You are a NIMCET exam expert tutor. Explain this question solution in simple Hinglish " +
                        "(Hindi-English mix) step by step. Mention the key concept or formula used. Be friendly and encouraging.\n" +
                        "Question: %s\nOptions: A)%s B)%s C)%s D)%s\nCorrect Answer: %s\nAdmin Solution: %s",
                q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                q.getCorrectAnswer(), q.getManualSolution()
        );
        return callGroq(prompt);
    }

    public String getHint(Long questionId) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        String prompt = String.format(
                "Give a very short hint (2-3 lines only) for this NIMCET question. " +
                        "Help the student think in right direction. Do NOT reveal the correct answer directly.\n" +
                        "Question: %s\nOptions: A)%s B)%s C)%s D)%s",
                q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()
        );
        return callGroq(prompt);
    }

    public String generateSimilar(Long questionId) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        String prompt = String.format(
                "Generate 1 new practice question similar to this NIMCET question. " +
                        "Same topic and difficulty level. " +
                        "Respond in pure JSON only, no extra text, no markdown:\n" +
                        "{\"question_text\":\"...\",\"option_a\":\"...\",\"option_b\":\"...\",\"option_c\":\"...\"," +
                        "\"option_d\":\"...\",\"correct_answer\":\"A/B/C/D\",\"solution\":\"...\"}\n" +
                        "Topic: %s\nDifficulty: %s\nOriginal Question: %s",
                q.getTopic(), q.getDifficulty(), q.getQuestionText()
        );
        return callGroq(prompt);
    }
}