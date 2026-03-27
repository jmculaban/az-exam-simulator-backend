package com.azexam.simulator.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.service.ExamResultService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
  
  private final ExamResultService examResultService;

  public ExamController(ExamResultService examResultService) {
    this.examResultService = examResultService;
  }

  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ExamResultResponse> submit(
      @PathVariable UUID sessionId) {

    return ResponseEntity.ok(
      examResultService.submitExam(sessionId)
    );
  }

  @GetMapping("/{sessionId}/result")
  public ResponseEntity<ExamResultResponse> getResult(@PathVariable UUID sessionId) {
      return ResponseEntity.ok(
        examResultService.getResult(sessionId)
      );
  }
}
