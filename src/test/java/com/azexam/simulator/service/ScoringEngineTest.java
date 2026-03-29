package com.azexam.simulator.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.azexam.simulator.model.yaml.QuestionYaml;
import com.azexam.simulator.service.scoring.ScoringEngine;
import com.azexam.simulator.service.scoring.SingleChoiceScorer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScoringEngineTest {
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldReturnTrueForCorrectSingleChoice() throws Exception {

    var scorer = new SingleChoiceScorer(objectMapper);

    var engine = new ScoringEngine(List.of(scorer));

    QuestionYaml question = new QuestionYaml();
    question.setType("SINGLE_CHOICE");
    question.setCorrectAnswer("A");

    String userAnswer = objectMapper.writeValueAsString("A");

    boolean result = engine.isCorrect(question, userAnswer);

    assertTrue(result);
  }

  @Test
  void shouldReturnFalseForWrongSingleChoice() throws Exception {

    var scorer = new SingleChoiceScorer(objectMapper);

    var engine = new ScoringEngine(List.of(scorer));

    QuestionYaml question = new QuestionYaml();
    question.setType("SINGLE_CHOICE");
    question.setCorrectAnswer("A");

    String userAnswer = objectMapper.writeValueAsString("B");

    boolean result = engine.isCorrect(question, userAnswer);

    assertFalse(result);
  }

  @Test
  void shouldThrowIfNoScorerFound() {

    var engine = new ScoringEngine(List.of());

    QuestionYaml question = new QuestionYaml();
    question.setType("UNKNOWN");

    assertThrows(RuntimeException.class, () -> 
      engine.isCorrect(question, "\"A\"")
    );
  }
}
