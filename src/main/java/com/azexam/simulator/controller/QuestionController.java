package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.FlagRequest;
import com.azexam.simulator.service.ExamQuestionStateService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/exam-state")
public class QuestionController {
  
  private final ExamQuestionStateService examQuestionStateService;

  public QuestionController(ExamQuestionStateService examQuestionStateService) {
    this.examQuestionStateService = examQuestionStateService;
  }

  @PostMapping("/{sessionId}/{questionId}/flag")
  public ResponseEntity<?> flagQuestion(
      @PathVariable UUID sessionId,
      @PathVariable String questionId,
      @RequestBody FlagRequest request) {

    examQuestionStateService.flag(
      sessionId,
      questionId,
      request.isFlagged()
    );

    return ResponseEntity.ok().build();
  }

  @PostMapping("/{questionId}/visit")
  public ResponseEntity<?> markVisited(
      @PathVariable String questionId,
      @RequestBody FlagRequest request) {

    examQuestionStateService.markVisited(
      request.getSessionId(),
      questionId
    );

    return ResponseEntity.ok().build();
  }
}
