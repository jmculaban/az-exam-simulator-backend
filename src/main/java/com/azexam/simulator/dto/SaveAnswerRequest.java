package com.azexam.simulator.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveAnswerRequest {
  
  private UUID sessionId;
  private String questionId;
  private Object answer;
}
