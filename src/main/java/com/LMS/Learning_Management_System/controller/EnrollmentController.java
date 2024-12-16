package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Enrollment;
import com.LMS.Learning_Management_System.service.EnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollInCourse(@RequestBody Enrollment enrollment , HttpServletRequest request) {
        try {
            enrollmentService.enrollInCourse(enrollment, request);
            return ResponseEntity.ok("Student enrolled successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }}
