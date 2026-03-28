package com.azexam.simulator.service.scoring;

import java.util.HashSet;
import java.util.List;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MultipleChoiceScorer implements QuestionScorer {
  
  private final ObjectMapper objectMapper;
  
  public MultipleChoiceScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Boolean supports(String type) {
    return "MULTIPLE_CHOICE".equals(type);
  }

  public Boolean isCorrect(QuestionYaml question, String json) {
    try {
      List<String> user = objectMapper.readValue(
        json, 
        new TypeReference<List<String>>() {}
      );
      
      return new HashSet<>(user)
        .equals(new HashSet<>(question.getCorrectAnswers()));
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
