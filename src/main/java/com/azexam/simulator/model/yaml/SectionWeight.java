package com.azexam.simulator.model.yaml;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Defines the percentage weight for a section in weighted random selection.
 */
@Data
@NoArgsConstructor
public class SectionWeight {
  private String sectionId;
  private Integer percentageWeight;
}
