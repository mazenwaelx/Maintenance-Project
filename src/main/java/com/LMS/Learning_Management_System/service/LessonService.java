package com.LMS.Learning_Management_System.service;
import com.LMS.Learning_Management_System.dto.LessonDto;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.CourseRepository;
import com.LMS.Learning_Management_System.repository.EnrollmentRepository;
import com.LMS.Learning_Management_System.repository.LessonRepository;
import com.LMS.Learning_Management_System.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LessonService(LessonRepository lessonRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void addLesson(Lesson lesson, HttpServletRequest request) {
        // auth
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }
        Course course = courseRepository.findById(lesson.getCourseId().getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));

        int ids = course.getInstructorId().getUserAccountId();
        if (loggedInInstructor.getUserId() != ids) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }


        if (lesson.getOTP() == null || lesson.getOTP().isEmpty()) {
            throw new IllegalArgumentException("OTP value cannot be null");
        }
        lesson.setCreationTime(new Date(System.currentTimeMillis()));
        if (lesson.getCourseId() == null || lesson.getCourseId().getCourseId() == 0) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }

        lesson.setCourseId(course);
        lessonRepository.save(lesson);
    }

    public List<LessonDto> getLessonsByCourseId(int courseId, HttpServletRequest request) {

        Course course = check_course_before_logic(courseId, request);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId()) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }
        List<Lesson> lessons = lessonRepository.findByCourseId(course);
        return convertToCoueDtoList(lessons, courseId);
    }

    public LessonDto getLessonById(int lessonId, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("No such LessonId: " + lessonId));
        return new LessonDto(
                lesson.getLessonId(),
                lesson.getCourseId().getCourseId(),
                lesson.getLessonName(),
                lesson.getLessonDescription(),
                lesson.getLessonOrder(),
                lesson.getOTP(),
                lesson.getContent(),
                lesson.getCreationTime()
        );
    }

    public void updateLesson(int lessonId, Lesson updatedLesson, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }
        Course course = courseRepository.findById(updatedLesson.getCourseId().getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));

        int ids = course.getInstructorId().getUserAccountId();
        if (loggedInInstructor.getUserId() != ids) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }

        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        existingLesson.setLessonName(updatedLesson.getLessonName());
        existingLesson.setLessonDescription(updatedLesson.getLessonDescription());
        existingLesson.setLessonOrder(updatedLesson.getLessonOrder());
        existingLesson.setContent(updatedLesson.getContent());
        existingLesson.setOTP(updatedLesson.getOTP());
        lessonRepository.save(existingLesson);
    }

    public void deleteLesson(int lessonId, int courseId, HttpServletRequest request) {
        Course course = check_course_before_logic(courseId, request);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId()) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }
        lessonRepository.deleteById(lessonId);
    }

    private List<LessonDto> convertToCoueDtoList(List<Lesson> lessons, int courseId) {
        return lessons.stream()
                .map(lesson -> new LessonDto(
                        lesson.getLessonId(),
                        courseId,
                        lesson.getLessonName(),
                        lesson.getLessonDescription(),
                        lesson.getLessonOrder(),
                        lesson.getOTP(),
                        lesson.getContent(),
                        lesson.getCreationTime()
                ))
                .collect(Collectors.toList());
    }

    private Course check_course_before_logic(int courseId, HttpServletRequest request) {
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
            throw new IllegalArgumentException("You are not authorized to show or delete or update this course.");
        }
        return existingCourse;
    }


    public void StudentEnterLesson(int courseId, int lessonId,String otp, HttpServletRequest request)
    {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if(loggedInInstructor ==null)
        {
            throw new IllegalArgumentException("No user is logged in.");
        }
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        List<Enrollment>enrollments = enrollmentRepository.findByCourse(existingCourse);
        int flag=0;
        for (Enrollment enrollment : enrollments) {
            if(enrollment.getStudent().getUserAccountId() == loggedInInstructor.getUserId()){
                flag = 1;
            }
        }
        if(flag==0)
            throw new IllegalArgumentException("You are not enrolled to this course.");

        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        if (!Objects.equals(existingLesson.getOTP(), otp))
        {
            throw new IllegalArgumentException("OTP does not match.");
        }

    }
}
