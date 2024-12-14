package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Optional<Student> findById(int userId) {
        return studentRepository.findById(userId);
    }

    public void save(Student student) {
        studentRepository.save(student); // Save the student entity
    }
}
