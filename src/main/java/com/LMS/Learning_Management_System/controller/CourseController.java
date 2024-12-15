package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Course;
import com.LMS.Learning_Management_System.entity.Lesson;
import com.LMS.Learning_Management_System.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.service.*;
@RestController
@RequestMapping("/api/course")
public class CourseController {


    private final CourseService courseService;
    private final LessonService lessonService;
    public CourseController(CourseService courseService, LessonService lessonService) {
        this.courseService = courseService;
        this.lessonService=lessonService;
    }


    @PostMapping("/add")
    public ResponseEntity<String> addCourse(@RequestBody Course course){
        try {
            courseService.addCourse(course);
            return ResponseEntity.ok("Course created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addLesson")
    public ResponseEntity<String> addLesson(@RequestBody Lesson lesson){
        try {
            lessonService.addLesson(lesson);
            return ResponseEntity.ok("Lesson added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
