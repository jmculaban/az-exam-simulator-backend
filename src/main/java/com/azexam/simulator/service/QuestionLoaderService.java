package com.azexam.simulator.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.azexam.simulator.model.yaml.ExamYaml;
import com.azexam.simulator.model.yaml.QuestionYaml;
import com.azexam.simulator.model.yaml.SectionYaml;
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
  @Cacheable("exam")
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

    if (exam.getRandomQuestionCount() == null || exam.getRandomQuestionCount() <= 0) {
      throw new RuntimeException("Exam randomQuestionCount must be a positive number");
    }

    if (exam.getSections() == null || exam.getSections().isEmpty()) {
      throw new RuntimeException("Exam has no sections");
    }

    // Validate section weights if defined
    if (exam.getSectionWeights() != null && !exam.getSectionWeights().isEmpty()) {
      int totalWeight = 0;
      for (var weight : exam.getSectionWeights()) {
        if (weight.getPercentageWeight() == null || weight.getPercentageWeight() <= 0) {
          throw new RuntimeException("Section weight must be positive: " + weight.getSectionId());
        }
        totalWeight += weight.getPercentageWeight();
      }
      if (totalWeight != 100) {
        throw new RuntimeException("Section weights must sum to 100, got: " + totalWeight);
      }
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

  /**
   * Counts all questions across sections.
   */
  public int getTotalQuestionCount(ExamYaml exam) {
    return exam.getSections().stream()
      .mapToInt(section -> section.getQuestions().size())
      .sum();
  }

  /**
   * Randomly selects distinct question ids from the exam question pool.
   */
  public List<String> selectRandomQuestionIds(ExamYaml exam, int count) {
    var allIds = exam.getSections().stream()
      .flatMap(section -> section.getQuestions().stream())
      .map(QuestionYaml::getId)
      .toList();

    if (count > allIds.size()) {
      throw new RuntimeException("Requested question count exceeds available pool");
    }

    var mutable = new ArrayList<>(allIds);

    for (int i = mutable.size() - 1; i > 0; i--) {
      int j = ThreadLocalRandom.current().nextInt(i + 1);
      String tmp = mutable.get(i);
      mutable.set(i, mutable.get(j));
      mutable.set(j, tmp);
    }

    return List.copyOf(mutable.subList(0, count));
  }

  /**
   * Returns a copy of the exam that only includes selected question ids.
   */
  public ExamYaml filterExamByQuestionIds(ExamYaml exam, List<String> selectedIds) {
    Set<String> selected = new HashSet<>(selectedIds);

    var filteredSections = exam.getSections().stream()
      .map(section -> {
        var filteredQuestions = section.getQuestions().stream()
          .filter(question -> selected.contains(question.getId()))
          .toList();

        if (filteredQuestions.isEmpty()) {
          return null;
        }

        var copy = new SectionYaml();
        copy.setId(section.getId());
        copy.setTitle(section.getTitle());
        copy.setQuestions(filteredQuestions);
        return copy;
      })
      .filter(section -> section != null)
      .toList();

    var filteredExam = new ExamYaml();
    filteredExam.setExamCode(exam.getExamCode());
    filteredExam.setDescription(exam.getDescription());
    filteredExam.setDurationMinutes(exam.getDurationMinutes());
    filteredExam.setRandomQuestionCount(exam.getRandomQuestionCount());
    filteredExam.setSections(filteredSections);

    return filteredExam;
  }

  /**
   * Randomly selects questions by section with percentage-based weights.
   * Distributes the total question count across sections based on their weights.
   *
   * @param exam the exam with section weights
   * @param totalCount total number of questions to select (e.g., 50)
   * @return list of selected question ids distributed by weight
   */
  public List<String> selectRandomQuestionIdsByWeight(ExamYaml exam, int totalCount) {
    
    if (exam.getSectionWeights() == null || exam.getSectionWeights().isEmpty()) {
      // Fall back to flat random selection if no weights defined
      return selectRandomQuestionIds(exam, totalCount);
    }

    var selectedIds = new ArrayList<String>();
    var sectionWeightMap = new java.util.HashMap<String, Integer>();
    
    // Build map of section weights
    for (var weight : exam.getSectionWeights()) {
      sectionWeightMap.put(weight.getSectionId(), weight.getPercentageWeight());
    }

    // Map sections by id for quick lookup
    var sectionMap = exam.getSections().stream()
      .collect(java.util.stream.Collectors.toMap(SectionYaml::getId, s -> s));

    // Distribute questions across sections based on weights
    for (var weight : exam.getSectionWeights()) {
      String sectionId = weight.getSectionId();
      int percentageWeight = weight.getPercentageWeight();
      
      // Calculate questions needed for this section
      int questionsForSection = Math.round(totalCount * percentageWeight / 100f);
      
      // Get all question ids from this section
      var section = sectionMap.get(sectionId);
      if (section == null) {
        throw new RuntimeException("Section not found: " + sectionId);
      }

      var sectionQuestionIds = section.getQuestions().stream()
        .map(QuestionYaml::getId)
        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

      if (questionsForSection > sectionQuestionIds.size()) {
        throw new RuntimeException(
          "Section " + sectionId + " has only " + sectionQuestionIds.size() + 
          " questions but weight requires " + questionsForSection
        );
      }

      // Fisher-Yates shuffle and select
      for (int i = sectionQuestionIds.size() - 1; i > 0; i--) {
        int j = ThreadLocalRandom.current().nextInt(i + 1);
        String tmp = sectionQuestionIds.get(i);
        sectionQuestionIds.set(i, sectionQuestionIds.get(j));
        sectionQuestionIds.set(j, tmp);
      }

      selectedIds.addAll(sectionQuestionIds.subList(0, questionsForSection));
    }

    return selectedIds;
  }
}
