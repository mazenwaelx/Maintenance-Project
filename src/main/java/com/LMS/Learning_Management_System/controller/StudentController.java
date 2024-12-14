package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.service.StudentService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @PostMapping("/update_student")
    public void updateUser(@RequestParam Long userId, @RequestParam String firstName, @RequestParam String secondName) {
        Student student = studentService.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found"));
        student.setFirstName(firstName);
        student.setLastName(secondName);
        studentService.save(student);
    }
}
