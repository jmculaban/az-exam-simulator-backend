package com.azexam.simulator.service.scoring;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MatchingScorer {
  
  private final ObjectMapper objectMapper;

  public MatchingScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Boolean supports(String type) {
    return "MATCHING".equals(type);
  }

  public Boolean isCorrect(QuestionYaml question, String json) {
    try{
      Map<String, String> user = objectMapper.readValue(
        json,
        new TypeReference<Map<String, String>>() {}
      );

      return user.equals(question.getCorrectMap());
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
