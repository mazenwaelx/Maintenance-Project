package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class CourseService {
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    public CourseService(InstructorRepository instructorRepository,CourseRepository courseRepository, LessonRepository lessonRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    public void addCourse(Course course){
        if (courseRepository.findByCourseName(course.getCourseName()) != null) {
            throw new IllegalArgumentException("This CourseName already exist");
        }
        course.setCreationDate(new Date(System.currentTimeMillis()));
        if (course.getInstructorId() == null|| course.getInstructorId().getUserAccountId()==0) {
            throw new IllegalArgumentException("InstructorId cannot be null");
        }
        Instructor instructor = instructorRepository.findById(course.getInstructorId().getUserAccountId())
                .orElseThrow(() -> new IllegalArgumentException("No such Instructor"));
        course.setInstructorId(instructor);
        if(course.getLessons()!=null){
            List<Lesson> lessons= course.getLessons();
            for (Lesson lesson : lessons){
                if (lesson.getOTP() == null || lesson.getOTP().isEmpty())
                {
                    throw new IllegalArgumentException("OTP value cannot be null");
                }
                lesson.setCourseId(course);
                lesson.setCreationTime(new Date(System.currentTimeMillis()));
                lessonRepository.save(lesson);
            }
        }
        courseRepository.save(course);
    }

}
