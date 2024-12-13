package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.repository.StudentRepository;
import com.LMS.Learning_Management_System.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UsersRepository UsersRepository;
    public StudentService(StudentRepository studentRepository , UsersRepository UsersRepository) {
        this.studentRepository = studentRepository;
        this.UsersRepository = UsersRepository;
    }

    public Optional<Student> getOne(Integer id) {
        return studentRepository.findById(id);
    }
    public Student findById(int studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
    public void save(Student student) {
        studentRepository.save(student);
    }

}
