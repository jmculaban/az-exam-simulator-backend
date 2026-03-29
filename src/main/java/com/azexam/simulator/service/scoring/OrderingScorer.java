package com.azexam.simulator.service.scoring;

import java.util.List;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderingScorer implements QuestionScorer {
  
  private final ObjectMapper objectMapper;

  public OrderingScorer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean supports(String type) {
    return "ORDERING".equals(type);
  }

  /** {@inheritDoc} */
  @Override
  public Boolean isCorrect(QuestionYaml question, String json) {
    try {
      List<String> userOrder = objectMapper.readValue(
        json, 
        new TypeReference<List<String>>() {}
      );

      List<String> correctOrder = question.getCorrectOrder();

      if (userOrder.size() != correctOrder.size()) {
        return false;
      }

      for (int i = 0; i < userOrder.size(); i++) {
        String userItem = ScoringUtils.normalize(userOrder.get(i));
        String correctItem = ScoringUtils.normalize(correctOrder.get(i));

        if (!userItem.equals(correctItem)) {
          return false;
        }
      }

      return true;
      
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse user answer JSON: " + json, e);
    }
  }
}
