package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.Course;
import com.LMS.Learning_Management_System.entity.Enrollment;
import com.LMS.Learning_Management_System.entity.Student;
import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.repository.CourseRepository;
import com.LMS.Learning_Management_System.repository.EnrollmentRepository;
import com.LMS.Learning_Management_System.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;


    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public void enrollInCourse(Enrollment enrollmentRequest, HttpServletRequest request) {
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (loggedInUser.getUserId()!=enrollmentRequest.getStudent().getUserAccountId()) {
            throw new IllegalArgumentException("Student ID mismatch. Please provide the correct ID.");
        }

        Student student = studentRepository.findById(enrollmentRequest.getStudent().getUserAccountId())
                .orElseThrow(() -> new IllegalArgumentException("No student found with the given ID."));

        int courseId = enrollmentRequest.getCourse().getCourseId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (isEnrolled) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(new java.util.Date());

        enrollmentRepository.save(enrollment);
    }

}