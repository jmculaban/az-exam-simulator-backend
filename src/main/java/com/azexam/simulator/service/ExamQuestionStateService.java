package com.azexam.simulator.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.ExamQuestionState;
import com.azexam.simulator.repository.ExamQuestionStateRepository;

/**
 * Persists per-question UI state for an exam session.
 */
@Service
public class ExamQuestionStateService {
  
  private final ExamQuestionStateRepository examQuestionStateRepository;

  public ExamQuestionStateService(ExamQuestionStateRepository examQuestionStateRepository) {
    this.examQuestionStateRepository = examQuestionStateRepository;
  }

  /**
   * Sets the flagged state for a question and marks it visited.
   *
   * @param sessionId session id
   * @param questionId question id
   * @param flagged desired flagged state
   */
  public void flag(UUID sessionId, String questionId, boolean flagged) {

    var state = examQuestionStateRepository.findBySessionIdAndQuestionId(sessionId, questionId)
      .orElseGet(() -> {
        var newState = new ExamQuestionState();
        newState.setId(UUID.randomUUID());
        newState.setSessionId(sessionId);
        newState.setQuestionId(questionId);
        return newState;
      });
    
    state.setFlagged(flagged);
    state.setVisited(true);

    examQuestionStateRepository.save(state);
  }

  /**
   * Marks a question as visited for a session.
   *
   * @param sessionId session id
   * @param questionId question id
   */
  public void markVisited(UUID sessionId, String questionId) {

    var state = examQuestionStateRepository
      .findBySessionIdAndQuestionId(sessionId, questionId)
      .orElseGet(() -> {
        var newState = new ExamQuestionState();
        newState.setId(UUID.randomUUID());
        newState.setSessionId(sessionId);
        newState.setQuestionId(questionId);
        return newState;
      });
    
    state.setVisited(true);

    examQuestionStateRepository.save(state);
  }
}
