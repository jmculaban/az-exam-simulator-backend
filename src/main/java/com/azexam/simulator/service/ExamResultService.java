package com.azexam.simulator.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.AnswerDto;
import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamResult;
import com.azexam.simulator.repository.ExamResultRepository;

@Service
public class ExamResultService {
  
  private final ExamSessionService sessionService;
  private final ExamResultRepository resultRepository;
  private final QuestionLoaderService questionLoader;

  public ExamResultService(
    ExamSessionService sessionService,
    ExamResultRepository resultRepository,
    QuestionLoaderService questionLoader
  ) {
    this.sessionService = sessionService;
    this.resultRepository = resultRepository;
    this.questionLoader = questionLoader;
  }

  public ExamResultResponse submitExam(UUID sessionId, List<AnswerDto> answers) {
    
    var existing = resultRepository.findBySessionId(sessionId);
    
    if (existing.isPresent()) {
      throw new BadRequestException("Exam already submitted");
    }

    var session = sessionService.getSession(sessionId);
    var questions = questionLoader.loadExam(session.getExamCode()).getQuestions();

    int correct = 0;

    for (var q: questions) {
      var userAnswer = answers.stream()
        .filter(a -> a.getQuestionId().equals(q.getId()))
        .findFirst();

      if (userAnswer.isPresent() &&
          userAnswer.get().getAnswer().equals(q.getCorrectAnswer())) {
            correct++;
      }
    }

    int total = questions.size();
    int score = (correct * 100) / total;

    ExamResult result = new ExamResult();
    result.setId(UUID.randomUUID());
    result.setSessionId(sessionId);
    result.setScore(score);
    result.setCorrect(correct);
    result.setTotal(total);
    result.setPassed(score >= 70);

    resultRepository.save(result);

    return new ExamResultResponse(score, correct, total, score >= 70);
  }

  public ExamResultResponse getResult(UUID sessionId) {

    var result = resultRepository.findBySessionId(sessionId)
      .orElseThrow(() -> new RuntimeException("Exam result not found"));
    
    return new ExamResultResponse(
      result.getScore(),
      result.getCorrect(),
      result.getTotal(),
      result.getPassed()
    );
  }
}
