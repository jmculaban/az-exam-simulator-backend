package com.azexam.simulator.service.scoring;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MultipleChoiceScorer implements QuestionScorer {
  
  private final ObjectMapper objectMapper;
  
  public MultipleChoiceScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Boolean supports(String type) {
    return "MULTIPLE_CHOICE".equals(type);
  }

  @Override
  public Boolean isCorrect(QuestionYaml question, String json) {
    try {
      List<String> userAnswer = objectMapper.readValue(
        json, 
        new TypeReference<List<String>>() {}
      );

      List<String> correctAnswers = question.getCorrectAnswers();

      // Normalize both list
      var normalizedUser = userAnswer.stream()
        .map(ScoringUtils::normalize)
        .toList();
      
      var normalizedCorrect = correctAnswers.stream()
        .map(ScoringUtils::normalize)
        .toList();

      return normalizedUser.containsAll(normalizedCorrect)
        && normalizedCorrect.containsAll(normalizedUser);
        
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
