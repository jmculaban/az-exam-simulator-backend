package com.azexam.simulator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlagRequest {
  
  @NotNull(message = "Flag status is required")
  private boolean flagged;
}
