package com.azexam.simulator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerDto {
  private String questionId;
  private Object answer;
}
