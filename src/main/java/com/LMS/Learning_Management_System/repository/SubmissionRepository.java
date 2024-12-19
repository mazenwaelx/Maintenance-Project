package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubmissionRepository extends JpaRepository<Submission, Integer> {

    List<Submission> findByStudentId(Student student);
}
