package com.azexam.simulator.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewExamResponse {
  
  private UUID sessionId;
  private String userId;
  private Instant startTime;
  private Instant endTime;
  private Integer score;
  private Integer correct;
  private Integer total;
  private Boolean passed;

  private List<ReviewSectionDto> sections;
}
