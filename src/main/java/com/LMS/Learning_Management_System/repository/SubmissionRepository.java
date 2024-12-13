package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
}
