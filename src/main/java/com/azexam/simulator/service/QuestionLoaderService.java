package com.azexam.simulator.service;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.yaml.ExamYaml;
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

      return yamlMapper.readValue(yamlContent, ExamYaml.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load exam questions", e);
    }
  }
}
