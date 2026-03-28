package com.azexam.simulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamTimerResponse {
  
  private long remainingSeconds;
  private boolean expired;

  public ExamTimerResponse(long remainingSeconds, boolean expired) {
    this.remainingSeconds = remainingSeconds;
    this.expired = expired;
  }
}
