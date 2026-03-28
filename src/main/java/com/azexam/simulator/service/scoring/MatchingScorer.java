package com.azexam.simulator.service.scoring;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MatchingScorer implements QuestionScorer {
  
  private final ObjectMapper objectMapper;

  public MatchingScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Boolean supports(String type) {
    return "MATCHING".equals(type);
  }

  @Override
  public Boolean isCorrect(QuestionYaml question, String json) {
    try{
      Map<String, String> userMap= objectMapper.readValue(
        json,
        new TypeReference<Map<String, String>>() {}
      );

      Map<String, String> correctMap = question.getCorrectMap();

      // Size check
      if (userMap.size() != correctMap.size()) {
        return false;
      }

      return userMap.entrySet().stream()
        .allMatch(entry -> {
          String key = entry.getKey();

          if (!correctMap.containsKey(key)) return false;

          String userValue = entry.getValue().trim().toLowerCase();
          String correctValue = correctMap.get(key).trim().toLowerCase();

          return userValue.equals(correctValue);
        });
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
