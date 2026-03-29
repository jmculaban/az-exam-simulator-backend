package com.azexam.simulator.service.scoring;

import com.azexam.simulator.model.yaml.QuestionYaml;

public interface QuestionScorer {
  
  /**
   * Indicates whether this scorer supports the provided question type.
   *
   * @param type question type
   * @return true when supported
   */
  Boolean supports(String type);

  /**
   * Evaluates whether a user answer is correct for the given question.
   *
   * @param question question definition
   * @param userAnswerJson serialized user answer
   * @return true when answer is correct
   */
  Boolean isCorrect(QuestionYaml question, String userAnswerJson);
}
