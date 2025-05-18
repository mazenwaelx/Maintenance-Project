package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.dto.AssignmentFeedbackDto;

import com.LMS.Learning_Management_System.repository.*;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.dto.*;


import com.LMS.Learning_Management_System.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AssignmentFeedbackService {

    public AssignmentFeedbackService(AssignmentFeedbackRepository feedbackRepo, SubmissionRepository submissionRepo, NotificationsService notificationsService) {
        this.feedbackRepo = feedbackRepo;
        this.submissionRepo = submissionRepo;
        this.notificationsService = notificationsService;
    }

    private final AssignmentFeedbackRepository feedbackRepo;
    private final SubmissionRepository submissionRepo;
    private final NotificationsService notificationsService;


    public AssignmentFeedback provideFeedback(AssignmentFeedbackDto dto) {
        Submission submission = submissionRepo.findById(dto.getSubmissionId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        AssignmentFeedback feedback = new AssignmentFeedback();
        feedback.setSubmission(submission);
        feedback.setGrade(dto.getGrade());
        feedback.setComments(dto.getComments());

        AssignmentFeedback saved = feedbackRepo.save(feedback);

        // Notify student
        String studentEmail = submission.getStudent().getUserAccount().getEmail();
        notificationsService.sendNotification("Feedback available for your submission.", submission.getStudent().getUserAccount().getUserId());



        return saved;
    }

    public Optional<AssignmentFeedback> getFeedbackForSubmission(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId.intValue()).orElseThrow(() -> new RuntimeException("Submission not found"));

        return feedbackRepo.findBySubmission(submission);
    }
}
