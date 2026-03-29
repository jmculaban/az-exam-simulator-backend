package com.azexam.simulator.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewExamResponse {
  
  private UUID sessionId;
  private Integer score;
  private Integer correct;
  private Integer total;
  private Boolean passed;

  private List<ReviewSectionDto> sections;
}
