package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.dto.StudentDto;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final WaitlistRepository waitlistRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             WaitlistRepository waitlistRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.waitlistRepository = waitlistRepository;
    }

    public void enrollInCourse(Enrollment enrollment, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (enrollment.getStudent().getUserAccountId() != user.getUserId()) {
            throw new IllegalArgumentException("Student ID mismatch. Please provide the correct ID.");
        }

        Optional<Student> studentOpt = studentRepository.findById(enrollment.getStudent().getUserAccountId());
        Optional<Course> courseOpt = courseRepository.findById(enrollment.getCourse().getCourseId());

        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("No student found with the given ID.");
        }

        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("No course found with the given ID: " + enrollment.getCourse().getCourseId());
        }

        Student student = studentOpt.get();
        Course course = courseOpt.get();

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        List<Course> prerequisites = course.getPrerequisites();
        for (Course prerequisite : prerequisites) {
            boolean completed = enrollmentRepository.existsByStudentAndCourse(student, prerequisite);
            if (!completed) {
                throw new IllegalArgumentException("Student must complete prerequisite course: " + prerequisite.getCourseName());
            }
        }

        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            waitlistRepository.save(new Waitlist(student, course));
            throw new IllegalArgumentException("Course is full. You have been added to the waitlist.");
        }

        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);
    }

    public List<StudentDto> viewEnrolledStudents(int courseId, HttpServletRequest request) {
        Course course = check_course_before_logic(courseId, request);
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        List<Student> students = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            students.add(enrollment.getStudent());
        }
        return convertToDtoList(students);
    }

    private List<StudentDto> convertToDtoList(List<Student> students) {
        return students.stream()
                .map(student -> new StudentDto(
                        student.getUserAccountId(),
                        student.getFirstName(),
                        student.getLastName()
                ))
                .collect(Collectors.toList());
    }

    public void removeEnrolledStudent(int studentId, int courseId, HttpServletRequest request) {
        Course course = check_course_logic(courseId, request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No student found with the given ID."));

        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);
        if (!optionalEnrollment.isPresent()) {
            throw new IllegalArgumentException("This student is not enrolled in this course");
        }

        Enrollment enrollment = optionalEnrollment.get();
        enrollmentRepository.deleteById(enrollment.getEnrollmentId());
        course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
        courseRepository.save(course);

        Optional<Waitlist> nextInLine = waitlistRepository.findFirstByCourseOrderByTimestampAsc(course);
        if (nextInLine.isPresent()) {
            Student waitlistedStudent = nextInLine.get().getStudent();
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudent(waitlistedStudent);
            newEnrollment.setCourse(course);
            enrollmentRepository.save(newEnrollment);
            course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
            courseRepository.save(course);
            waitlistRepository.delete(nextInLine.get());
            // Optionally notify the student
        }
    }

    private Course check_course_before_logic(int courseId, HttpServletRequest request) {
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }
        if (loggedInUser.getUserTypeId() == null || loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            throw new IllegalArgumentException("Logged-in user is not an Instructor or Admin.");
        }
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 &&
                existingCourse.getInstructorId().getUserAccountId() != loggedInUser.getUserId()) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }
        return existingCourse;
    }

    private Course check_course_logic(int courseId, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        if (existingCourse.getInstructorId() == null ||
                existingCourse.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId()) {
            throw new IllegalArgumentException("You are not the Instructor of this course.");
        }
        return existingCourse;
    }

    private boolean studentHasCompletedCourse(Student student, Course prerequisite) {
        return enrollmentRepository.existsByStudentAndCourse(student, prerequisite);
    }
}
