package com.azexam.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NavigationDto {
  
  private int totalQuestions;
  private int answered;
  private int flagged;
  private int notVisited;
}
