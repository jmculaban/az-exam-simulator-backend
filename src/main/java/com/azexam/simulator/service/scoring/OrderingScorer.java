package com.azexam.simulator.service.scoring;

import java.util.List;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderingScorer {
  
  private final ObjectMapper objectMapper;

  public OrderingScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Boolean supports(String type) {
    return "ORDERING".equals(type);
  }

  public Boolean isCorrect(QuestionYaml question, String json) {
    try {
      List<String> user = objectMapper.readValue(
        json, 
        new TypeReference<List<String>>() {}
      );
      return user.equals(question.getCorrectAnswers());
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
