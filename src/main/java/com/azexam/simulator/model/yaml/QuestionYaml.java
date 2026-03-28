package com.azexam.simulator.model.yaml;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionYaml {
  private String id;
  private String text;
  private String type;
  
  // OPTIONS
  private List<String> options;
  private Map<String, String> optionMap;

  // SINGLE_CHOICE
  private String correctAnswer;

  // MULTIPLE_CHOICE
  private List<String> correctAnswers;

  // ORDERING
  private List<String> correctOrder;

  // MATCHING
  private Map<String , String> correctMap;
}
