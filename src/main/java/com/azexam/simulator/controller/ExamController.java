package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.service.ExamService;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/exam-sessions")
public class ExamController {
  
  private final ExamService service;

  public ExamController(ExamService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ExamSession createSession(@RequestBody Map<String, String> request) {
      return service.createSession(request.get("examCode"));
  }
}
