package com.azexam.simulator.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectionResponseDto {
  
  private String sectionId;
  private String title;
  private List<QuestionResponse> questions;
}
