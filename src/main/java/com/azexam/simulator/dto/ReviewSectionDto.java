package com.azexam.simulator.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSectionDto {
  
  private String id;
  private String title;
  private List<ReviewQuestionDto> questions;
}
