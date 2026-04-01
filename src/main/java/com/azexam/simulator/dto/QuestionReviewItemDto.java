package com.azexam.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionReviewItemDto {
  private String id;
  private String text;
  private boolean answered;
  private boolean flagged;
}
