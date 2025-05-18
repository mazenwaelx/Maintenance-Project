package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.AssignmentFeedback;
import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentFeedbackRepository extends JpaRepository<AssignmentFeedback, Long> {
    Optional<AssignmentFeedback> findBySubmission(Submission submission);

}
