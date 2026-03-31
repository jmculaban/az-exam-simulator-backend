package com.azexam.simulator.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewQuestionDto {
  
  private String id;
  private String text;
  private String type;

  private List<String> options;
  private Map<String, String> optionMap;

  private Object userAnswer;
  private Object correctAnswer;

  private Boolean isCorrect;
}
