package com.azexam.simulator.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectionReviewResponse {
  private UUID sessionId;
  private String examCode;
  private String description;
  private ExamTimerResponse timer;
  private NavigationDto navigation;
  private List<SectionReviewItemDto> sections;
}
