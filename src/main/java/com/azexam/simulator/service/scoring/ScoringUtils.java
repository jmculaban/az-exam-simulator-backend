package com.azexam.simulator.service.scoring;

public class ScoringUtils {
  
  /**
   * Normalizes text for comparison by trimming and lowercasing.
   *
   * @param value raw value
   * @return normalized value or empty string when input is null
   */
  public static String normalize(String value) {
    if (value == null) return "";
    return value.trim().toLowerCase();
  }
}
