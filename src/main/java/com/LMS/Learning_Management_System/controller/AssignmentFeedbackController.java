package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.dto.AssignmentFeedbackDto;
import com.LMS.Learning_Management_System.entity.AssignmentFeedback;
import com.LMS.Learning_Management_System.service.AssignmentFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")

public class AssignmentFeedbackController {

    private final AssignmentFeedbackService feedbackService;
    public AssignmentFeedbackController(AssignmentFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    @PostMapping("/provide")
    public AssignmentFeedback provideFeedback(@RequestBody AssignmentFeedbackDto dto) {
        return feedbackService.provideFeedback(dto);
    }

    @GetMapping("/view/{submissionId}")
    public Optional<AssignmentFeedback> getFeedback(@PathVariable Long submissionId) {
        return feedbackService.getFeedbackForSubmission(submissionId);
    }
}
