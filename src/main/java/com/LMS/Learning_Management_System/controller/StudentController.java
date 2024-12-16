package com.LMS.Learning_Management_System.controller;


import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @PutMapping("/update_profile/{studentId}")
    public ResponseEntity<String> updateUser(@PathVariable int studentId,
                           @RequestBody Student student,
                           HttpServletRequest request
    ) {
        try {
            studentService.save(studentId, student, request);
            return ResponseEntity.ok("Student updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
