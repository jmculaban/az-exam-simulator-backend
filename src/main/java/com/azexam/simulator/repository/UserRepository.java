package com.azexam.simulator.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azexam.simulator.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByExternalUserId(String externalUserId);
  
}