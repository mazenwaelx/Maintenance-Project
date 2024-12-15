package com.LMS.Learning_Management_System.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grading")
public class Grading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private int gradingId;
    @Column(name="grade")
    private int grade;
    @ManyToOne
    @JoinColumn(name = "quiz_id",referencedColumnName = "quiz_id")
    private Quiz quiz_id;

    @ManyToOne
    @JoinColumn(name="student_id",referencedColumnName = "user_account_id")
    private Student student_id;

    public Grading(){}

    public Grading(int gradingId, int grade, Quiz quiz_id, Student student_id) {
        this.gradingId = gradingId;
        this.grade = grade;
        this.quiz_id = quiz_id;
        this.student_id = student_id;
    }

    public int getGradingId() {
        return gradingId;
    }

    public void setGradingId(int gradingId) {
        this.gradingId = gradingId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Quiz getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Quiz quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Student getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Student student_id) {
        this.student_id = student_id;
    }
}
