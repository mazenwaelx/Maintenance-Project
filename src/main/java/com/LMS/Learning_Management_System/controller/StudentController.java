package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.repository.UsersRepository;
import com.LMS.Learning_Management_System.service.StudentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;
    private final UsersRepository UsersRepository;
    public StudentController(StudentService studentService, UsersRepository usersRepository) {
        this.studentService = studentService;
        this.UsersRepository = usersRepository;
    }
    @PostMapping("/add-name")
    public ResponseEntity<String> addFirstAndLastName(
            @RequestParam("studentId") int studentId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName) {
        Student student = studentService.findById(studentId);
        if (student != null) {
            student.setFirstName(firstName);
            student.setLastName(lastName);
            studentService.save(student);
            return ResponseEntity.ok("First and last name added successfully for Student ID: " + studentId);
        } else {
            return ResponseEntity.badRequest().body("Student not found with ID: " + studentId);
        }
    }

    @GetMapping("/full-name")
    public ResponseEntity<String> getFullName(@RequestParam("studentId") int studentId) {
        Student student = studentService.findById(studentId);
        if (student != null) {
            String fullName = student.getFirstName() + " " + student.getLastName();
            return ResponseEntity.ok("Full name: " + fullName);
        } else {
            return ResponseEntity.badRequest().body("Student not found with ID: " + studentId);
        }
    }
}
