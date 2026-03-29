package com.azexam.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
  
  private String id;
  private String text;
  private String type;

  private Object options;
  private Object answer;

  private boolean isAnswered;
  private boolean isFlagged;
  private boolean visited;
}
