package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Enrollment;
import com.LMS.Learning_Management_System.entity.Grading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradingRepository extends JpaRepository<Grading, Integer> {
}
