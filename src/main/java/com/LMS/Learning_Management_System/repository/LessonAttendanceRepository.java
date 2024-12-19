package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Lesson;
import com.LMS.Learning_Management_System.entity.LessonAttendance;
import com.LMS.Learning_Management_System.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonAttendanceRepository extends JpaRepository<LessonAttendance, Integer> {
    boolean existsByLessonIdAndStudentId(Lesson lessonId , Student studentId);
}
