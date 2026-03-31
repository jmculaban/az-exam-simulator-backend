package com.azexam.simulator.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionResponse {
  
  private String id;
  private String text;
  private String type;

  private List<String> options;
  private Map<String, String> optionMap;
  private Object answer;

  private boolean isAnswered;
  private boolean isFlagged;
  private boolean visited;
}
