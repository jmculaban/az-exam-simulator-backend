package com.azexam.simulator.service;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.yaml.ExamYaml;
import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Loads and validates exam definitions from YAML files in blob storage.
 */
@Service
public class QuestionLoaderService {
  
  private final BlobService blobService;
  private final ObjectMapper yamlMapper;

  public QuestionLoaderService(BlobService blobService) {
    this.blobService = blobService;
    this.yamlMapper = new ObjectMapper(new YAMLFactory())
      .findAndRegisterModules();
  }

  /**
   * Loads an exam by exam code from blob storage.
   *
   * @param examCode exam identifier
   * @return parsed and validated exam payload
   */
  public ExamYaml loadExam(String examCode) {

    try {
      String fileName = examCode.toLowerCase() + ".yml";

      String yamlContent = blobService.downloadFile(fileName);

      ExamYaml exam = yamlMapper.readValue(yamlContent, ExamYaml.class);

      validateExam(exam);

      return exam;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load exam: " + examCode, e);
    }
  }

  private void validateExam(ExamYaml exam) {

    if (exam.getSections() == null || exam.getSections().isEmpty()) {
      throw new RuntimeException("Exam has no sections");
    }

    for (var section : exam.getSections()) {

      if (section.getQuestions() == null || section.getQuestions().isEmpty()) {
        throw new RuntimeException("Section has no questions: " + section.getId());
      }

      for (QuestionYaml question : section.getQuestions()) {
        validateQuestion(question);
      }
    }
  }

  private void validateQuestion(QuestionYaml question) {

    if (question.getType() == null) {
      throw new RuntimeException("Question type is missing: " + question.getId());
    }

    switch (question.getType().toUpperCase()) {
      
      case "SINGLE_CHOICE":
        if (question.getOptions() == null || question.getCorrectAnswer() == null) {
          throw new RuntimeException("Invalid SINGLE_CHOICE: " + question.getId());
        }
        break;
      case "MULTIPLE_CHOICE":
        if (question.getOptions() == null || question.getCorrectAnswers() == null) {
          throw new RuntimeException("Invalid MULTIPLE_CHOICE: " + question.getId());
        }
        break;
      case "ORDERING":
        if (question.getOptions() == null || question.getCorrectOrder() == null) {
          throw new RuntimeException("Invalid ORDERING: " + question.getId());
        }
        break;
      case "MATCHING":
        if (question.getOptionMap() == null || question.getCorrectMap() == null) {
          throw new RuntimeException("Invalid MATCHING: " + question.getId());
        }
        break;
      
      default:
        throw new RuntimeException("Unsupported question type: " + question.getType());
    }
  }
}
