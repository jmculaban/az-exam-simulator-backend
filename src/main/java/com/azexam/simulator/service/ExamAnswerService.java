package com.azexam.simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.SaveAnswerRequest;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamAnswerService {
  
  private final ExamAnswerRepository examAnswerRepository;
  private final ExamSessionService examSessionService;
  private final ObjectMapper objectMapper;

  public ExamAnswerService(
      ExamAnswerRepository examAnswerRepository, 
      ExamSessionService examSessionService,
      ObjectMapper objectMapper) {
    this.examAnswerRepository = examAnswerRepository;
    this.examSessionService = examSessionService;
    this.objectMapper = objectMapper;
  }

  public void saveAnswer(SaveAnswerRequest request) {
    
    // Check if answer already exists for this question in the session
    var existing = examAnswerRepository.findBySessionIdAndQuestionId(
      request.getSessionId(), 
      request.getQuestionId()
    );

    ExamAnswer answer;

    if (existing.isPresent()) {
      answer = existing.get();
      
      try {
        String jsonAnswer = objectMapper.writeValueAsString(request.getAnswer());
        answer.setAnswer(jsonAnswer);
      } catch (Exception e) {
        throw new RuntimeException("Failed to serialize answer to JSON", e);
      }

      answer.setUpdatedAt(Instant.now());
      answer.setUpdatedBy("system");
    } else {
      answer = new ExamAnswer();
      answer.setId(UUID.randomUUID());
      answer.setSessionId(request.getSessionId());
      answer.setQuestionId(request.getQuestionId());
      
      try {
        String jsonAnswer = objectMapper.writeValueAsString(request.getAnswer());
        answer.setAnswer(jsonAnswer);
      } catch (Exception e) {
        throw new RuntimeException("Failed to serialize answer to JSON", e);
      }
      
      answer.setCreatedAt(Instant.now());
      answer.setUpdatedAt(Instant.now());
      answer.setUpdatedBy("system");
    }

    examAnswerRepository.save(answer);
  }

  public List<ExamAnswer> getAnswers(UUID sessionId) {
    return examAnswerRepository.findBySessionId(sessionId);
  }
}
