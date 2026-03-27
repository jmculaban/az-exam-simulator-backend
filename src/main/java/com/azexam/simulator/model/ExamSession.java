package com.azexam.simulator.model;

import java.time.LocalDateTime;
import java.util.UUID;

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
@Table(name = "exam_session")
public class ExamSession {
  
  @Id
  private UUID id;

  private String examCode;
  private String status;
  private Integer durationMinutes;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
