package com.azexam.simulator.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartExamRequest {
  
  @NotBlank(message = "Exam code is required")
  private String examCode;

  @NotNull(message = "User ID is required")
  private UUID userId;
}
