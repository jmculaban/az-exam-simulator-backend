package com.azexam.simulator.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.User;
import com.azexam.simulator.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User ensureUser(String externalUserId, String email) {
    String normalizedExternalId = normalizeExternalUserId(externalUserId);
    UUID internalId = toInternalUserId(normalizedExternalId);

    return userRepository.findByExternalUserId(normalizedExternalId)
      .or(() -> userRepository.findById(internalId))
      .orElseGet(() -> {
        User user = new User();
        user.setId(internalId);
        user.setExternalUserId(normalizedExternalId);
        user.setEmail(resolveEmail(internalId, email));
        return userRepository.save(user);
      });
  }

  private String normalizeExternalUserId(String externalUserId) {
    if (externalUserId == null || externalUserId.isBlank()) {
      throw new IllegalArgumentException("User ID is required");
    }
    return externalUserId.trim();
  }

  public UUID toInternalUserId(String externalUserId) {
    try {
      return UUID.fromString(externalUserId);
    } catch (IllegalArgumentException ignored) {
      // Non-UUID input is supported by deterministic UUID mapping.
      return UUID.nameUUIDFromBytes(externalUserId.getBytes(StandardCharsets.UTF_8));
    }
  }

  private String resolveEmail(UUID userId, String email) {
    if (email != null && !email.isBlank()) {
      return email.trim();
    }
    return "user-" + userId + "@local.simulator";
  }
}
