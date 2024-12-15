package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


//@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseName(String courseName);
}
