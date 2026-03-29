package com.azexam.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewQuestionDto {
  
  private String id;
  private String text;
  private String type;

  private Object options;

  private Object userAnswer;
  private Object correctAnswer;

  private Boolean isCorrect;
}
