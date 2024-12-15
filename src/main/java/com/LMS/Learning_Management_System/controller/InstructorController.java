package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Instructor;
import com.LMS.Learning_Management_System.service.InstructorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }
    @PostMapping("/update_instructor")
    public void updateUser(@RequestParam Long userId, @RequestParam String firstName, @RequestParam String secondName) {
        Instructor instructor = instructorService.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found"));
        instructor.setFirstName(firstName);
        instructor.setLastName(secondName);
        instructorService.save(instructor);
    }
}
