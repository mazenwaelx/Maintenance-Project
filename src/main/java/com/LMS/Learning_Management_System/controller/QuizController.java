package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.dto.CourseDto;
import com.LMS.Learning_Management_System.dto.GradingDto;
import com.LMS.Learning_Management_System.dto.QuestionDto;
import com.LMS.Learning_Management_System.dto.QuizDto;
import com.LMS.Learning_Management_System.entity.Course;
import com.LMS.Learning_Management_System.repository.QuizRepository;
import com.LMS.Learning_Management_System.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController( QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quiz_id/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable int id, HttpServletRequest request) {
        try {
            QuizDto quizDTO = quizService.getQuizByID(id , request);
            return ResponseEntity.ok(quizDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add_quiz")
    public ResponseEntity<?> addQuiz(@RequestBody QuizDto quizDto, HttpServletRequest request)
    {
        try {
            quizService.Create(quizDto.getCourse_id(),quizDto.getType());
            return ResponseEntity.ok("Quiz created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/add_questions_bank")
    public ResponseEntity<?> addQuestionsBank(@RequestBody QuizDto quizDto, HttpServletRequest request)
    {
        try {
            quizService.createQuestionBank(quizDto.getCourse_id(),quizDto.getQuestionList());
            return ResponseEntity.ok("Question bank created successfully for the course id: "+quizDto.getCourse_id());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/add_questions")
    public ResponseEntity<?> addQuestions(@RequestBody QuestionDto questionDto, HttpServletRequest request)
    {
        try {
            quizService.addQuestion(questionDto);
            return ResponseEntity.ok("Question added successfully for the course id: "+questionDto.getCourse_id());
        } catch (Exception  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get_question_bank/{id}")
    public ResponseEntity<?> getQuestionBank(@PathVariable int id, HttpServletRequest request)
    {
        try {
            QuizDto quizDto=quizService.getQuestionBank(id);
            return ResponseEntity.ok(quizDto.getQuestionList());
        } catch (Exception  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/grade_quiz")
    public ResponseEntity<?> gradeQuiz(@RequestBody GradingDto gradingDto, HttpServletRequest request)
    {
        try {
            quizService.gradeQuiz(gradingDto);
            return ResponseEntity.ok("Quiz has been graded for the student with ID: "+gradingDto.getStudent_id());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get student quiz grades
    @GetMapping("/get_quiz_grade/{quiz_id}/student/{student_id}")
    public ResponseEntity<?> getQuizGradeByStudent(@PathVariable int quiz_id,@PathVariable int student_id, HttpServletRequest request)
    {
        try {
            int grade=quizService.quizFeedback(quiz_id,student_id);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get quiz questions
    @GetMapping("/get_quiz_questions/{id}")
    public ResponseEntity<?> getQuizQuestions(@PathVariable int id, HttpServletRequest request)
    {
        try {
            return ResponseEntity.ok(quizService.getQuizQuestions(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/grades/{quizId}")
    public ResponseEntity <List<String>> trackQuizGrades (@PathVariable int quizId, HttpServletRequest request)
    {
        try
        {
            List <String> submissions = quizService.quizGrades(quizId, request);
            return ResponseEntity.ok(submissions);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(Collections.singletonList(e.getMessage()));
        }
    }
}
