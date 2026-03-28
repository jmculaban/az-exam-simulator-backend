package com.azexam.simulator.service.scoring;

public class ScoringUtils {
  
  public static String normalize(String value) {
    if (value == null) return "";
    return value.trim().toLowerCase();
  }
}
