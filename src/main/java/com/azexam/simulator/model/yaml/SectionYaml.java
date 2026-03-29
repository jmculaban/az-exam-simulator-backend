package com.azexam.simulator.model.yaml;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionYaml {
  private String id;
  private String title;
  private List<QuestionYaml> questions;
}
