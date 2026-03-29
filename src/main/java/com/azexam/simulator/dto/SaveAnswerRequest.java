package com.azexam.simulator.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveAnswerRequest {
  
  @NotNull(message = "Session ID is required")
  private UUID sessionId;

  @NotBlank(message = "Question ID is required")
  private String questionId;

  @NotNull(message = "Answer cannot be null")
  private Object answer;
}
