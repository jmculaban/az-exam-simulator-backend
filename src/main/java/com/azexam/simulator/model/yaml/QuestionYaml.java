package com.azexam.simulator.model.yaml;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionYaml {
  private String id;
  private String text;
  private List<String> options;
  private String correctAnswer;
}
