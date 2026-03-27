package com.azexam.simulator.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSessionRequest {
  private String examCode;
  private UUID userId;
}
