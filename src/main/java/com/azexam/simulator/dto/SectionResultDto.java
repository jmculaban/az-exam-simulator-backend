package com.azexam.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectionResultDto {
  
  private String sectionId;
  private String title;

  private Integer correct;
  private Integer total;
  private Double score;
}
