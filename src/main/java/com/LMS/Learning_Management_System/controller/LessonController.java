package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.dto.LessonDto;
import com.LMS.Learning_Management_System.entity.Lesson;
import com.LMS.Learning_Management_System.service.LessonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
public class LessonController {
    private final LessonService lessonService;
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }
    @PostMapping("/add_lesson")
    public ResponseEntity<String> addLesson(@RequestBody Lesson lesson , HttpServletRequest request){
        try {
            lessonService.addLesson(lesson , request);
            return ResponseEntity.ok("Lesson added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/get_all_lessons/{courseId}")
    public ResponseEntity<?> getAllLessons(@PathVariable int courseId , HttpServletRequest request) {
        try {
            List<LessonDto> lessons = lessonService.getLessonsByCourseId(courseId , request);
            return ResponseEntity.ok(lessons);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // get all lesson for specifing course id
    // get specific lesson
    // update specific lesson
    // delete specific lesson
}
