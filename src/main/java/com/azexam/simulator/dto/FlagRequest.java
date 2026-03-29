package com.azexam.simulator.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlagRequest {
  
  @NotNull(message = "Session ID is required")
  private UUID sessionId;

  @NotNull(message = "Flag status is required")
  private boolean flagged;
}
