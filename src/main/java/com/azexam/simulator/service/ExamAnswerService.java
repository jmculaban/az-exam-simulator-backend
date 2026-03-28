package com.azexam.simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.SaveAnswerRequest;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamAnswerService {
  
  private final ExamAnswerRepository examAnswerRepository;
  private final ExamSessionService sessionService;
  private final ObjectMapper objectMapper;

  public ExamAnswerService(
      ExamAnswerRepository examAnswerRepository,
      ExamSessionService sessionService,
      ObjectMapper objectMapper) {
    this.examAnswerRepository = examAnswerRepository;
    this.sessionService = sessionService;
    this.objectMapper = objectMapper;
  }

  public void saveAnswer(SaveAnswerRequest request) {
    
    var session = sessionService.getSession(request.getSessionId());

    if ("SUBMITTED".equals(session.getStatus())) {
      throw new BadRequestException("Cannot save answer for submitted exam");
    }
    
    // Check if answer already exists for this question in the session
    var existing = examAnswerRepository.findBySessionIdAndQuestionId(
      request.getSessionId(), 
      request.getQuestionId()
    );

    ExamAnswer answer;

    if (existing.isPresent()) {
      answer = existing.get();
      answer.setAnswer(toJson(request.getAnswer()));
      answer.setUpdatedAt(Instant.now());
      answer.setUpdatedBy("system");
    } else {
      answer = new ExamAnswer();
      answer.setId(UUID.randomUUID());
      answer.setSessionId(request.getSessionId());
      answer.setQuestionId(request.getQuestionId());
      answer.setAnswer(toJson(request.getAnswer()));
      answer.setCreatedAt(Instant.now());
      answer.setUpdatedAt(Instant.now());
      answer.setUpdatedBy("system");
    }

    if (request.getSessionId() == null || request.getQuestionId() == null) {
      throw new BadRequestException("Session ID and Question ID are required");
    }

    examAnswerRepository.save(answer);
  }

  public List<ExamAnswer> getAnswers(UUID sessionId) {
    return examAnswerRepository.findBySessionId(sessionId);
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize answer to JSON", e);
    }
  }
}
