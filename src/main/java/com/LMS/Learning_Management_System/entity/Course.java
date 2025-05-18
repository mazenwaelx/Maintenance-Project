package com.LMS.Learning_Management_System.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer  courseId;

    private String courseName;
    private int capacity;
    @Transient // Not persisted, calculate dynamically
    private int currentEnrollment;
    // when delete course did not delete instructor
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "instructor_id", referencedColumnName = "user_account_id")
    private Instructor instructorId;

    private String description;

    @Column(name = "media", nullable = true, length = 64)
    private String media;

    private int duration;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    @OneToMany(mappedBy = "courseId")
    private List<Lesson> lessons;

    // New field for prerequisites
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private List<Course> prerequisites;

    public Course() {
    }

    public Course(int courseId, String courseName, Instructor instructorId, String description, String media, int duration, Date creationDate, List<Course> prerequisites) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.description = description;
        this.media = media;
        this.duration = duration;
        this.creationDate = creationDate;
        this.lessons = lessons;
        this.prerequisites = prerequisites;
    }

    public int getCourseId() {
        return courseId;
    }
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Instructor getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Instructor instructorId) {
        this.instructorId = instructorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", instructorId=" + instructorId +
                ", description='" + description + '\'' +
                ", media='" + media + '\'' +
                ", duration=" + duration +
                ", creationDate=" + creationDate +
                ", prerequisites=" + prerequisites +
                '}';
    }
}
