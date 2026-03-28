package com.azexam.simulator.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserExamHistoryResponse {
  
  private UUID sessionId;
  private String examCode;
  private Integer score;
  private Boolean passed;
  private Instant submittedAt;

  public UserExamHistoryResponse(
      UUID sessionId, 
      String examCode, 
      Integer score, 
      Boolean passed, 
      Instant submittedAt) {
    this.sessionId = sessionId;
    this.examCode = examCode;
    this.score = score;
    this.passed = passed;
    this.submittedAt = submittedAt;
  }
}
