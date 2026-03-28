package com.azexam.simulator.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResumeExamResponse {
  
  private UUID sessionId;
  private String status;
  private List<?> questions;
  private Map<String, Object> answers;

  public ResumeExamResponse(
      UUID sessionId, 
      String status, 
      List<?> questions, 
      Map<String, Object> answers) {
    this.sessionId = sessionId;
    this.status = status;
    this.questions = questions;
    this.answers = answers;
  }
}
