package com.LMS.Learning_Management_System.dto;




public class AssignmentFeedbackDto {
    public int getGrade() {
        return grade;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    private Long submissionId;
    private int grade;
    private String comments;
}
