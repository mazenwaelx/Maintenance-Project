package com.LMS.Learning_Management_System.service;


import com.LMS.Learning_Management_System.dto.CourseDto;
import com.LMS.Learning_Management_System.dto.LessonDto;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.CourseRepository;
import com.LMS.Learning_Management_System.repository.LessonRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    public LessonService(LessonRepository lessonRepository,CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository=courseRepository;
    }

    public void addLesson(Lesson lesson , HttpServletRequest request ){
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
        if(loggedInInstructor.getUserId()!= ids){
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }


        if (lesson.getOTP() == null || lesson.getOTP().isEmpty())
        {
            throw new IllegalArgumentException("OTP value cannot be null");
        }
        lesson.setCreationTime(new Date(System.currentTimeMillis()));
        if (lesson.getCourseId()== null|| lesson.getCourseId().getCourseId()==0) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }

        lesson.setCourseId(course);
        lessonRepository.save(lesson);
    }

    public List<LessonDto>  getLessonsByCourseId(int courseId , HttpServletRequest request) {

        Course course = check_course_before_logic(courseId , request);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if(course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId())
        {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }
       List<Lesson>lessons =lessonRepository.findByCourseId(course);
       return convertToCoueDtoList(lessons , courseId);
    }

    private List<LessonDto> convertToCoueDtoList(List<Lesson> lessons ,int courseId) {
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

    private Course check_course_before_logic(int courseId, HttpServletRequest request)
    {
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
}
