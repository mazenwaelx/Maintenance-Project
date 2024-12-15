package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.Instructor;
import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class InstructorService {
    private final InstructorRepository instructorRepository;
    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }
    public Optional<Instructor> findById(int userId) {
        return instructorRepository.findById(userId);
    }

    public void save(Instructor instructor) {
        instructorRepository.save(instructor);
    }

}
