package com.azexam.simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.SaveAnswerRequest;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.model.ExamQuestionState;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamQuestionStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles persistence of answers and related question-state updates.
 */
@Service
public class ExamAnswerService {
  
  private final ExamAnswerRepository examAnswerRepository;
  private final ExamQuestionStateRepository examQuestionStateRepository;
  private final ExamSessionService sessionService;
  private final ObjectMapper objectMapper;

  public ExamAnswerService(
      ExamAnswerRepository examAnswerRepository,
      ExamQuestionStateRepository examQuestionStateRepository,
      ExamSessionService sessionService,
      ObjectMapper objectMapper) {
    this.examAnswerRepository = examAnswerRepository;
    this.examQuestionStateRepository = examQuestionStateRepository;
    this.sessionService = sessionService;
    this.objectMapper = objectMapper;
  }

  /**
   * Saves or updates a user's answer for a question in a session.
   *
   * @param request answer request payload
   */
  public void saveAnswer(SaveAnswerRequest request) {
    
    // Validation
    if (request.getSessionId() == null || request.getQuestionId() == null) {
      throw new BadRequestException("Session ID and Question ID are required");
    }

    var session = sessionService.getSession(request.getSessionId());

    if ("SUBMITTED".equals(session.getStatus())) {
      throw new BadRequestException("Cannot save answer for submitted exam");
    }

    String jsonAnswer = toJson(request.getAnswer());
    
    try {
      var existing = examAnswerRepository.findBySessionIdAndQuestionId(
        request.getSessionId(), 
        request.getQuestionId()
      );

      ExamAnswer answer;

      if (existing.isPresent()) {
        answer = existing.get();
        answer.setAnswer(jsonAnswer);
        answer.setUpdatedAt(Instant.now());
        answer.setUpdatedBy("system");
      } else {
        answer = new ExamAnswer();
        answer.setId(UUID.randomUUID());
        answer.setSessionId(request.getSessionId());
        answer.setQuestionId(request.getQuestionId());
        answer.setAnswer(jsonAnswer);
        answer.setCreatedAt(Instant.now());
        answer.setUpdatedAt(Instant.now());
        answer.setUpdatedBy("system");
      }

      examAnswerRepository.save(answer);
    } catch (DataIntegrityViolationException e) {
      
      // Handle race condition
      var retryAnswer = examAnswerRepository.findBySessionIdAndQuestionId(
        request.getSessionId(), 
        request.getQuestionId()
      ).orElseThrow(() -> new RuntimeException("Retry failed after duplicate"));

      retryAnswer.setAnswer(jsonAnswer);
      retryAnswer.setUpdatedAt(Instant.now());

      examAnswerRepository.save(retryAnswer);
    }

    // Update question state
    upsertQuestionState(request.getSessionId(), request.getQuestionId());
  }

  /**
   * Retrieves all saved answers for the provided session.
   *
   * @param sessionId exam session id
   * @return list of saved answers
   */
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

  private void upsertQuestionState(UUID sessionId, String questionId) {
    try {
      var existing = examQuestionStateRepository
        .findBySessionIdAndQuestionId(sessionId, questionId);

      if (existing.isPresent()) {
        var state = existing.get();
        state.setVisited(true);

        examQuestionStateRepository.save(state);
      } else {
        var state = new ExamQuestionState();
        state.setId(UUID.randomUUID());
        state.setSessionId(sessionId);
        state.setQuestionId(questionId);
        state.setVisited(true);
        state.setFlagged(false);

        examQuestionStateRepository.save(state);
      }
    } catch (DataIntegrityViolationException e) {
      var retry = examQuestionStateRepository
        .findBySessionIdAndQuestionId(sessionId, questionId)
        .orElseThrow(() -> new RuntimeException("Retry failed after duplicate"));
      
      retry.setVisited(true);
      examQuestionStateRepository.save(retry);
    }

  }
}
