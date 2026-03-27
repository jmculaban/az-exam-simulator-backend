package com.azexam.simulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamResultResponse {
  private int score;
  private int correct;
  private int total;
  private boolean passed;

  public ExamResultResponse(int score, int correct, int total, boolean passed) {
    this.score = score;
    this.correct = correct;
    this.total = total;
    this.passed = passed;
  }
}
