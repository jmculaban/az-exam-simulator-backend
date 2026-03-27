package com.azexam.simulator.model.yaml;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamYaml {
  private String examCode;
  private List<QuestionYaml> questions;
}
