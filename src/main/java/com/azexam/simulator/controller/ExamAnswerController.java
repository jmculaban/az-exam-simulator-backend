package com.azexam.simulator.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.SaveAnswerRequest;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.service.ExamAnswerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exam-answers")
public class ExamAnswerController {
  
  private static final Logger log = LoggerFactory.getLogger(ExamAnswerController.class);

  private final ExamAnswerService examAnswerService;

  public ExamAnswerController(ExamAnswerService examAnswerService) {
    this.examAnswerService = examAnswerService;
  }

  /**
   * Saves or updates an answer for a question in an exam session.
   *
   * @param request answer payload
   * @return empty success response
   */
  @PostMapping
  public ResponseEntity<Void> saveAnswer(@Valid @RequestBody SaveAnswerRequest request) {
    
    log.info("Saving answer: sessionId={}, questionId={}", 
      request.getSessionId(), request.getQuestionId());
    
    examAnswerService.saveAnswer(request);

    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves all stored answers for a session.
   *
   * @param sessionId exam session id
   * @return list of answer records
   */
  @GetMapping("/session/{sessionId}")
  public ResponseEntity<List<ExamAnswer>> getAnswers(@PathVariable UUID sessionId) {
    log.info("Fetching answers: sessionId={}", sessionId);

    return ResponseEntity.ok(examAnswerService.getAnswers(sessionId));
  }
}
