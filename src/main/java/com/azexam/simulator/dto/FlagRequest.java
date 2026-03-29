package com.azexam.simulator.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlagRequest {
  
  private UUID sessionId;
  private boolean flagged;
}
