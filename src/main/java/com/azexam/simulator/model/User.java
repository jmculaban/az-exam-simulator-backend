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
@Table(name = "users")
public class User {
  
  @Id
  private UUID id;

  @Column(name = "external_user_id", nullable = false, unique = true)
  private String externalUserId;

  private String email;
}
