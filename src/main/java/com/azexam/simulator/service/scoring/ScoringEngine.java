package com.azexam.simulator.service.scoring;

import java.util.List;

import org.springframework.stereotype.Component;

import com.azexam.simulator.model.yaml.QuestionYaml;

@Component
public class ScoringEngine {
  
  private final List<QuestionScorer> scorers;

  public ScoringEngine(List<QuestionScorer> scorers) {
    this.scorers = scorers;
  }

  /**
   * Checks whether a user answer is correct for a question.
   *
   * @param question question definition
   * @param userAnswerJson serialized user answer payload
   * @return true when answer is correct
   */
  public boolean isCorrect(QuestionYaml question, String userAnswerJson) {

    return scorers.stream()
      .filter(s -> s.supports(question.getType()))
      .findFirst()
      .orElseThrow(() -> 
        new RuntimeException("No scorer for type: " + question.getType()))
      .isCorrect(question, userAnswerJson);
  }
}
