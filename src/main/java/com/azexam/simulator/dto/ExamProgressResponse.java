package com.azexam.simulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamProgressResponse {
  
  private int answered;
  private int total;
  private int percentage;

  public ExamProgressResponse(int answered, int total) {
    this.answered = answered;
    this.total = total;
    this.percentage = total == 0 ? 0 : (answered * 100) / total;
  }
}
