package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.FlagRequest;
import com.azexam.simulator.service.ExamQuestionStateService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST endpoints for per-question state tracking in an exam session.
 */
@RestController
@RequestMapping("/api/exam-state")
public class QuestionController {
  
  private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

  private final ExamQuestionStateService examQuestionStateService;

  public QuestionController(ExamQuestionStateService examQuestionStateService) {
    this.examQuestionStateService = examQuestionStateService;
  }

  /**
   * Sets or unsets the flagged state for a question.
   *
   * @param sessionId exam session id
   * @param questionId question id
   * @param request flag request containing desired state
   * @return empty success response
   */
  @PostMapping("/{sessionId}/{questionId}/flag")
  public ResponseEntity<?> flagQuestion(
      @PathVariable UUID sessionId,
      @PathVariable String questionId,
      @Valid @RequestBody FlagRequest request) {

    log.info("Flagging question: sessionId={}, questionId={}, flagged={}", 
      sessionId, questionId, request.isFlagged());
    
    examQuestionStateService.flag(
      sessionId,
      questionId,
      request.isFlagged()
    );

    return ResponseEntity.ok().build();
  }

  /**
   * Marks a question as visited.
   *
   * @param sessionId exam session id
   * @param questionId question id
   * @return empty success response
   */
  @PostMapping("/{sessionId}/{questionId}/visit")
  public ResponseEntity<?> markVisited(
      @PathVariable UUID sessionId,
      @PathVariable String questionId) {

    
    log.info("Marking question as visited: sessionId={}, questionId={}", sessionId, questionId);
    
    examQuestionStateService.markVisited(
      sessionId,
      questionId
    );

    return ResponseEntity.ok().build();
  }
}
