package com.azexam.simulator.service;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.yaml.ExamYaml;
import com.azexam.simulator.model.yaml.QuestionYaml;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Service
public class QuestionLoaderService {
  
  private final BlobService blobService;
  private final ObjectMapper yamlMapper;

  public QuestionLoaderService(BlobService blobService) {
    this.blobService = blobService;
    this.yamlMapper = new ObjectMapper(new YAMLFactory());
  }

  public ExamYaml loadExam(String examCode) {

    try {
      String fileName = examCode.toLowerCase() + ".yml";

      String yamlContent = blobService.downloadFile(fileName);

      ExamYaml exam = yamlMapper.readValue(yamlContent, ExamYaml.class);

      validateExam(exam);

      return exam;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load exam: " + examCode + " > " + e.getMessage());
    }
  }

  private void validateExam(ExamYaml exam) {

    if (exam.getQuestions() == null || exam.getQuestions().isEmpty()) {
      throw new RuntimeException("Exam has no questions");
    }

    for (QuestionYaml question : exam.getQuestions()) {
      validateQuestion(question);
    }
  }

  private void validateQuestion(QuestionYaml question) {

    if (question.getType() == null) {
      throw new RuntimeException("Question type is missing: " + question.getId());
    }

    switch (question.getType()) {
      
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
