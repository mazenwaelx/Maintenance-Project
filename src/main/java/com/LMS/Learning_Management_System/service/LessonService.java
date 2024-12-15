package com.LMS.Learning_Management_System.service;


import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.CourseRepository;
import com.LMS.Learning_Management_System.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    public LessonService(LessonRepository lessonRepository,CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository=courseRepository;
    }

    public void addLesson(Lesson lesson){
        if (lesson.getOTP() == null || lesson.getOTP().isEmpty())
        {
            throw new IllegalArgumentException("OTP value cannot be null");
        }
        lesson.setCreationTime(new Date(System.currentTimeMillis()));
        if (lesson.getCourseId()== null|| lesson.getCourseId().getCourseId()==0) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }
        Course course = courseRepository.findById(lesson.getCourseId().getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));
        lesson.setCourseId(course);
        lessonRepository.save(lesson);
    }

}
