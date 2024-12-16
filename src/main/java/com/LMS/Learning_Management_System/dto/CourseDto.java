package com.LMS.Learning_Management_System.dto;

// this Dto for get course by , id , we send the important data for course not all course data , that is the data transfer object

public class CourseDto {
    private int courseId;
    private String courseName;
    private int instructorId;
    private String description;
    private int duration;

    public CourseDto() {
    }

    public CourseDto(int courseId, String courseName, int instructorId, String description, int duration) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.description = description;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
