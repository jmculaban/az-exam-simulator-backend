package com.azexam.simulator.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.ResumeExamResponse;
import com.azexam.simulator.service.ExamQueryService;
import com.azexam.simulator.service.ExamResultService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
  
  private final ExamResultService examResultService;
  private final ExamQueryService examQueryService;

  public ExamController(
      ExamResultService examResultService,
      ExamQueryService examQueryService) {
    this.examResultService = examResultService;
    this.examQueryService = examQueryService;
  }

  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ExamResultResponse> submit(
      @PathVariable UUID sessionId) {

    return ResponseEntity.ok(
      examResultService.submitExam(sessionId)
    );
  }

  @GetMapping("/{sessionId}/result")
  public ResponseEntity<ExamResultResponse> getResult(
        @PathVariable UUID sessionId) {
      
      return ResponseEntity.ok(
        examResultService.getResult(sessionId)
      );
  }

  @GetMapping("/{sessionId}/resume")
  public ResponseEntity<ResumeExamResponse> resumeExam(
        @PathVariable UUID sessionId) {
    
    return ResponseEntity.ok(
      examQueryService.resumeExam(sessionId)
    );
  }
}
