package com.azexam.simulator.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionResponse {
  private String id;
  private String question;
  private List<String> choices;

  public QuestionResponse(String id, String question, List<String> choices) {
    this.id = id;
    this.question = question;
    this.choices = choices;
  }
}
