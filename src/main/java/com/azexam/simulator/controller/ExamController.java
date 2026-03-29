package com.azexam.simulator.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.StartExamRequest;
import com.azexam.simulator.dto.ExamProgressResponse;
import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.ExamTimerResponse;
import com.azexam.simulator.dto.ResumeExamResponse;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.service.ExamQueryService;
import com.azexam.simulator.service.ExamResultService;
import com.azexam.simulator.service.ExamSessionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
  
  private static final Logger log = LoggerFactory.getLogger(ExamController.class);

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

  /**
   * Creates a new exam session for a user.
   *
   * @param request start exam request payload
   * @return created exam session
   */
  @PostMapping("/start")
  public ResponseEntity<ExamSession> createSession(@Valid @RequestBody StartExamRequest request) {
    
    log.info("Creating exam session: userId={}, examCode={}", request.getUserId(), request.getExamCode());

    return ResponseEntity.status(HttpStatus.CREATED).body(
      examService.createSession(
        request.getExamCode(), 
        request.getUserId()
      )
    );
  }

  /**
   * Retrieves a session by its identifier.
   *
   * @param sessionId exam session id
   * @return session details
   */
  @GetMapping("/{sessionId}")
  public ResponseEntity<ExamSession> getSession(@PathVariable UUID sessionId) {
    
    log.info("Fetching exam session details: sessionId={}", sessionId);
    
    return ResponseEntity.ok(examService.getSession(sessionId));
  }

  /**
   * Submits an exam and calculates the final result.
   *
   * @param sessionId exam session id
   * @return computed exam result
   */
  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ExamResultResponse> submit(
      @PathVariable UUID sessionId) {

    log.info("Submitting exam: sessionId={}", sessionId);
    
    return ResponseEntity.ok(
      examResultService.submitExam(sessionId)
    );
  }

  /**
   * Fetches the persisted result for a submitted exam.
   *
   * @param sessionId exam session id
   * @return exam result payload
   */
  @GetMapping("/{sessionId}/result")
  public ResponseEntity<ExamResultResponse> getResult(
        @PathVariable UUID sessionId) {
      
    log.info("Fetching exam result: sessionId={}", sessionId);
      
    return ResponseEntity.ok(
      examResultService.getResult(sessionId)
    );
  }

  /**
   * Returns all data required to resume an ongoing exam.
   *
   * @param sessionId exam session id
   * @return resume payload with sections, questions, and timer
   */
  @GetMapping("/{sessionId}/resume")
  public ResponseEntity<ResumeExamResponse> resumeExam(
        @PathVariable UUID sessionId) {
    
    log.info("Resuming exam: sessionId={}", sessionId);
    
    return ResponseEntity.ok(
      examQueryService.resumeExam(sessionId)
    );
  }

  /**
   * Returns progress statistics for an exam session.
   *
   * @param sessionId exam session id
   * @return answered and total question counts
   */
  @GetMapping("/{sessionId}/progress")
  public ResponseEntity<ExamProgressResponse> getProgress(
        @PathVariable UUID sessionId) {
    
    log.info("Fetching exam progress: sessionId={}", sessionId);
    
    return ResponseEntity.ok(
      examQueryService.getProgress(sessionId)
    );
  }

  /**
   * Returns timer status for an exam session.
   *
   * @param sessionId exam session id
   * @return timer response with remaining seconds and expiration flag
   */
  @GetMapping("/{sessionId}/timer")
  public ResponseEntity<ExamTimerResponse> getTimer(
        @PathVariable UUID sessionId) {
    
    log.info("Fetching exam timer: sessionId={}", sessionId);
    
    return ResponseEntity.ok(
      examQueryService.getTimer(sessionId)
    );
  }
}
