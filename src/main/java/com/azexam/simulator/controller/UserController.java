package com.azexam.simulator.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.service.ExamQueryService;

/**
 * REST endpoints related to user-level exam querying.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  
  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final ExamQueryService examQueryService;

  public UserController(ExamQueryService examQueryService) {
    this.examQueryService = examQueryService;
  }

  /**
   * Retrieves paginated exam history for a user.
   *
   * @param userId user id
   * @param page zero-based page index
   * @param size page size
   * @param passed optional pass/fail filter
   * @param examCode optional exam code filter
   * @return paginated user exam history
   */
  @GetMapping("/{userId}/exam-history")
  public ResponseEntity<?> getExamHistory(
      @PathVariable UUID userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Boolean passed,
      @RequestParam(required = false) String examCode) {
    
    log.info("Fetching exam history: userId={}, passed={}, examCode={}, page={}, size={}", 
      userId, passed, examCode, page, size);
    
    var result = examQueryService.getUserExamHistory(
      userId, 
      passed, 
      examCode, 
      page, 
      size
    );

    return ResponseEntity.ok(result);
  }
}
