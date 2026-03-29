package com.azexam.simulator.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.CreateSessionRequest;
import com.azexam.simulator.dto.ExamProgressResponse;
import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.ExamTimerResponse;
import com.azexam.simulator.dto.ResumeExamResponse;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.service.ExamQueryService;
import com.azexam.simulator.service.ExamResultService;
import com.azexam.simulator.service.ExamSessionService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
  
  private final ExamSessionService examService;
  private final ExamResultService examResultService;
  private final ExamQueryService examQueryService;

  public ExamController(
      ExamSessionService examService,
      ExamResultService examResultService,
      ExamQueryService examQueryService) {
    this.examService = examService;
    this.examResultService = examResultService;
    this.examQueryService = examQueryService;
  }

  @PostMapping("/start")
  public ResponseEntity<ExamSession> createSession(@RequestBody CreateSessionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      examService.createSession(
        request.getExamCode(), 
        request.getUserId()
      )
    );
  }

  @GetMapping("/{sessionId}")
  public ResponseEntity<ExamSession> getSession(@PathVariable UUID sessionId) {
      return ResponseEntity.ok(examService.getSession(sessionId));
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

  @GetMapping("/{sessionId}/progress")
  public ResponseEntity<ExamProgressResponse> getProgress(
        @PathVariable UUID sessionId) {
    
    return ResponseEntity.ok(
      examQueryService.getProgress(sessionId)
    );
  }

  @GetMapping("/{sessionId}/timer")
  public ResponseEntity<ExamTimerResponse> getTimer(
        @PathVariable UUID sessionId) {
    
    return ResponseEntity.ok(
      examQueryService.getTimer(sessionId)
    );
  }
}
