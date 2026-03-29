package com.azexam.simulator.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumeExamResponse {
  
  private UUID sessionId;
  private String status;
  private String examCode;

  private ExamTimerResponse timer;
  private List<SectionResponseDto> sections;
  private NavigationDto navigationDto;
}
