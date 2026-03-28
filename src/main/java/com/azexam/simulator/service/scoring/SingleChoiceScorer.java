package com.azexam.simulator.service.scoring;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SingleChoiceScorer implements QuestionScorer {
  
  private final ObjectMapper objectMapper;

  public SingleChoiceScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Boolean supports(String type) {
    return "SINGLE_CHOICE".equals(type);
  }

  @Override
  public Boolean isCorrect(QuestionYaml question, String json) {
    try {
      String user = objectMapper.readValue(json, String.class);
      
      return ScoringUtils.normalize(user)
        .equals(ScoringUtils.normalize(question.getCorrectAnswer()));
        
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
