package com.azexam.simulator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartExamRequest {
  
  @NotBlank(message = "Exam code is required")
  private String examCode;

  @NotBlank(message = "User ID is required")
  private String userId;
}
