package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Instructor;
import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.service.InstructorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }
    @PutMapping("/update_profile/{instructorId}")
    public ResponseEntity<String> updateUser(@PathVariable int instructorId,
                                             @RequestBody Instructor instructor,
                                             HttpServletRequest request
    ) {
        try {
            instructorService.save(instructorId, instructor, request);
            return ResponseEntity.ok("Instructor updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
