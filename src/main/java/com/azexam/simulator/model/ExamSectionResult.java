package com.azexam.simulator.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "exam_section_result")
public class ExamSectionResult {
  
  @Id
  private UUID id;

  @Column(name = "session_id")
  private UUID sessionId;

  private String sectionId;
  private String title;

  private Integer correct;
  private Integer total;
  private Double score;
}
