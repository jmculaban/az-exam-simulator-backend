package com.azexam.simulator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnsureUserRequest {

  @NotBlank
  private String id;

  private String email;
}
