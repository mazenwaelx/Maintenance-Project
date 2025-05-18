package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.dto.AssignmentDto;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorRepository instructorRepository;
    private final NotificationsRepository notificationsRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository,
                             CourseRepository courseRepository, StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository,InstructorRepository instructorRepository,NotificationsRepository notificationsRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notificationsRepository = notificationsRepository;
    }

    public void uploadAssignment(AssignmentDto assignmentDto, HttpServletRequest request) {
        Users user = (Users) request.getSession().getAttribute("user");

        // Allow both instructors and students
        if (user == null || (user.getUserType().getUserTypeId() != 3 && user.getUserType().getUserTypeId() != 2)) {
            throw new IllegalArgumentException("Only instructors and students can upload assignments.");
        }

        Course course = courseRepository.findById(assignmentDto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDto.getTitle());
        assignment.setDescription(assignmentDto.getDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime dueDate = LocalDateTime.parse(assignmentDto.getDueDate(), formatter);
        assignment.setDueDate(dueDate);
        assignment.setCourse(course);

        Instructor instructor = instructorRepository.findById(assignmentDto.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        assignment.setInstructor(instructor);

        assignmentRepository.save(assignment);

        // If student uploaded the assignment, auto-create submission
        if (user.getUserType().getUserTypeId() == 2) {
            Student student = studentRepository.findByUserAccountId(user.getUserId());
            if (student == null) {
                throw new IllegalArgumentException("Student record not found.");
            }

            Submission submission = new Submission();
            submission.setAssignmentId(assignment);
            submission.setStudent(student);
            submission.setContent("Auto-submitted by student during upload.");
            submission.setSubmittedAt(new java.util.Date());
            submission.setGrade(null);
            submission.setFeedback(null);

            submissionRepository.save(submission);
        }
    }


    public void gradeAssignment(int studentID, int assigID, float grade, HttpServletRequest request ) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("You are not logged in");
        }

        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(()-> new IllegalArgumentException("Assignment not found"));


        if (loggedInInstructor.getUserId() != assignment.getCourse().getInstructorId().getUserAccountId()){
            throw new IllegalArgumentException("You're not the instructor of this course");
        }


        Student student = studentRepository.findById(studentID)
                .orElseThrow(()-> new IllegalArgumentException("Student not found"));

        List<Submission> submission = submissionRepository.findByStudent(student);


        if (submission.isEmpty()) {
            throw new IllegalArgumentException("Student has no submissions");
        }

        for (Submission s : submission) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                s.setGrade(grade);
                submissionRepository.save(s);
                return;
            }
        }
        throw new IllegalArgumentException("Student didn't submit this assignment");

    }

    public void saveAssignmentFeedback(int studentID, int assigID, String feedback, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("You are not logged in");
        }

        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (loggedInInstructor.getUserId() != assignment.getCourse().getInstructorId().getUserAccountId()) {
            throw new IllegalArgumentException("You're not the instructor of this course");
        }

        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Submission> submissionList = submissionRepository.findByStudent(student);
        if (submissionList.isEmpty()) {
            throw new IllegalArgumentException("Student has no submissions");
        }

        for (Submission s : submissionList) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                s.setFeedback(feedback);
                submissionRepository.save(s);

                // === Create a notification for the student ===
                Notifications notification = new Notifications();
                notification.setUser(student.getUserAccount()); // assumes Student → Users relation
                notification.setMessage("Feedback added for assignment: " + assignment.getTitle());
                notification.setCreatedAt(new java.util.Date());
                notification.setRead(false);

                notificationsRepository.save(notification);
                return;
            }
        }

        throw new IllegalArgumentException("Student didn't submit this assignment");
    }

    public String getFeedback(int assigID, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("You are not logged in");
        }
        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(()-> new IllegalArgumentException("Assignment not found"));


        Student student = studentRepository.findById(loggedInInstructor.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("You're not a student"));

        Boolean isExist = enrollmentRepository.existsByStudentAndCourse(student, assignment.getCourse());
        if (!isExist) {
            throw new IllegalArgumentException("You're not enrolled in this course");
        }

        List<Submission> submission = submissionRepository.findByStudent(student);


        if (submission.isEmpty()) {
            throw new IllegalArgumentException("Student has no submissions");
        }

        String feedback = "";

        for (Submission s : submission) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                if (s.getFeedback() == null){
                    feedback =  "There is no feedback yet";
                    break;

            }
                else {
                    feedback =  s.getFeedback();
                    break;
                }
        }
        throw new IllegalArgumentException("Student didn't submit this assignment");
    }
        return feedback;
    }

    public List <String> assignmentSubmissions (int assignmentId, HttpServletRequest request)
    {
        if (assignmentRepository.existsById(assignmentId))
        {
            Assignment assignment = assignmentRepository.findById(assignmentId).get();
            List <Submission> assignmentSubmissions = submissionRepository.findAllByAssignmentId(assignment);
            Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
            int instructorId = assignment.getCourse().getInstructorId().getUserAccountId();

            if (loggedInInstructor == null)
            {
                throw new IllegalArgumentException("No logged in user is found.");
            }
            else if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3)
            {
                throw new IllegalArgumentException("Logged-in user is not an instructor.");
            }
            else if (instructorId != loggedInInstructor.getUserId())
            {
                throw new IllegalArgumentException("Logged-in instructor does not have access for this assignment submissions.");
            }

            List <String> submissions = new ArrayList<>();
            for (Submission submission : assignmentSubmissions)
            {
                Student student = submission.getStudent();
                String studentSubmission = student.getUserAccountId() + ": " + submission.getContent();
                submissions.add(studentSubmission);
            }
            return submissions;
        }
        else
        {
            throw new IllegalArgumentException("Assignment with ID " + assignmentId + " not found.");
        }
    }
}
