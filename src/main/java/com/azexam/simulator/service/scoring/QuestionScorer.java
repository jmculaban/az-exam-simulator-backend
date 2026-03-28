package com.azexam.simulator.service.scoring;

import com.azexam.simulator.model.yaml.QuestionYaml;

public interface QuestionScorer {
  
  Boolean supports(String type);

  Boolean isCorrect(QuestionYaml question, String userAnswerJson);
}
