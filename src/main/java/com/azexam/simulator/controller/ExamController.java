package com.azexam.simulator.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.SubmitExamRequest;
import com.azexam.simulator.service.ExamResultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/exams")
public class ExamController {
  
  private final ExamResultService examResultService;

  public ExamController(ExamResultService examResultService) {
    this.examResultService = examResultService;
  }

  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ExamResultResponse> submit(
      @PathVariable UUID sessionId,
      @RequestBody SubmitExamRequest request) {

    return ResponseEntity.ok(
      examResultService.submitExam(sessionId, request.getAnswers())
    );
  }

  @GetMapping("/{sessionId}/result")
  public ResponseEntity<ExamResultResponse> getResult(@PathVariable UUID sessionId) {
      return ResponseEntity.ok(
        examResultService.getResult(sessionId)
      );
  }
}
