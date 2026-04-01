package com.azexam.simulator.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "exam_session")
public class ExamSession {
  
  @Id
  private UUID id;

  private String examCode;
  private String status;
  private Integer durationMinutes;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "selected_question_ids", columnDefinition = "jsonb")
  private List<String> selectedQuestionIds;

  private Instant startTime;
  private Instant endTime;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
