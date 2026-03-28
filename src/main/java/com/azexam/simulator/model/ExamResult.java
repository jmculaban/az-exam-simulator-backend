package com.azexam.simulator.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "exam_result")
public class ExamResult {
  
  @Id
  private UUID id;

  @Column(name = "session_id")
  private UUID sessionId;

  private Integer score;
  private Integer correct;
  private Integer total;
  private Boolean passed;

  @Column(name = "submitted_at")
  private Instant submittedAt;

  @ManyToOne
  @JoinColumn(name = "session_id", insertable = false, updatable = false)
  private ExamSession session;
}
