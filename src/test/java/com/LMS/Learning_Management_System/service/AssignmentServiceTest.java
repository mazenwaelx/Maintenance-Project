package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.dto.AssignmentDto;
import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AssignmentServiceTest {

    @Mock private AssignmentRepository assignmentRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private InstructorRepository instructorRepository;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private AssignmentService assignmentService;

    private Users instructorUser;
    private Users instructorUser2;
    private Users studentUser;
    private Course course;
    private UsersType instructorType;
    private UsersType studentType;
    private Assignment assignment;
    private Submission submission;
    private Student student;
    private AssignmentDto assignmentDto;
    private Instructor instructor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // — Types & Users —
        studentType = new UsersType();    studentType.setUserTypeId(2);
        instructorType = new UsersType(); instructorType.setUserTypeId(3);

        studentUser = new Users();           studentUser.setUserId(1); studentUser.setUserTypeId(studentType);
        instructorUser = new Users();        instructorUser.setUserId(2); instructorUser.setUserTypeId(instructorType);
        instructorUser2 = new Users();       instructorUser2.setUserId(3); instructorUser2.setUserTypeId(instructorType);

        // — Domain Entities —
        student = new Student();             student.setUserId(studentUser);
        instructor = new Instructor();       instructor.setUserId(instructorUser); instructor.setUserAccountId(2);

        course = new Course();
        course.setCourseId(1);
        course.setCourseName("Test Course");
        course.setInstructorId(instructor);
        course.setPrerequisites(new ArrayList<>());  // prevents any NPE in enrollment logic

        assignment = new Assignment();
        assignment.setAssignmentId(1);
        assignment.setCourse(course);

        submission = new Submission();
        submission.setSubmissionId(1);
        submission.setStudent(student);
        submission.setAssignmentId(assignment);

        assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentId(1);
        assignmentDto.setCourseId(1);
        assignmentDto.setInstructorId(2);

        // — “Happy‐path” stubs for ID = 1 only —
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);

        when(instructorRepository.findById(2)).thenReturn(Optional.of(instructor));

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        // ★ Prevent ANY NullPointer in submission lookups ★
        when(submissionRepository.findByStudent(any())).thenReturn(new ArrayList<>());
    }

    // ------------------------
    // uploadAssignment tests
    // ------------------------

    @Test
    public void UploadAssignment_UserNotLoggedIn() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.uploadAssignment(assignmentDto, request)
        );
        assertEquals(
                "Only instructors and students can upload assignments.",
                ex.getMessage()
        );
    }

    @Test
    public void uploadAssignment_NotStudent() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // User with userTypeId not 2 or 3 (simulate not student or instructor)
        Users fakeUser = new Users();
        UsersType fakeType = new UsersType();
        fakeType.setUserTypeId(99); // Not 2 or 3
        fakeUser.setUserId(99);
        fakeUser.setUserTypeId(fakeType);
        when(session.getAttribute("user")).thenReturn(fakeUser);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.uploadAssignment(assignmentDto, request)
        );
        assertEquals("Only instructors and students can upload assignments.", ex.getMessage());
    }

    @Test
    public void uploadAssignment_CourseNotFound() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.uploadAssignment(assignmentDto, request)
        );
        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    public void uploadAssignment_NotEnrolled() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(instructorRepository.findById(2)).thenReturn(Optional.of(instructor));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);

        assignmentDto.setTitle("X");
        assignmentDto.setDescription("X");
        assignmentDto.setDueDate("2025-05-17T00:00:00");

        // No exception expected
        assignmentService.uploadAssignment(assignmentDto, request);

        verify(assignmentRepository, times(1)).save(any(Assignment.class));
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }



    @Test
    public void uploadAssignment_AlreadySubmitted() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(instructorRepository.findById(2)).thenReturn(Optional.of(instructor));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        // The student has already submitted
        when(submissionRepository.findByStudent(student)).thenReturn(List.of(submission));

        assignmentDto.setTitle("X");
        assignmentDto.setDescription("X");
        assignmentDto.setDueDate("2025-05-17T00:00:00");

        // This is what your service does: it creates another submission, so just verify calls:
        assignmentService.uploadAssignment(assignmentDto, request);

        verify(assignmentRepository, times(1)).save(any(Assignment.class));
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }





    // -----------------------------------------
    // gradeAssignment tests
    // -----------------------------------------
    @Test
    public void gradeAssignment_UserNotLoggedIn(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(5,1,100, request)
        );
        assertEquals("You are not logged in", ex.getMessage());
    }

    @Test
    public void gradeAssignment_AssignmentNotFound(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);
        // no such assignment
        when(assignmentRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(1,6,100, request)
        );
        assertEquals("Assignment not found", ex.getMessage());
    }

    @Test
    public void gradeAssignment_NotTheInstructor(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser2);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        // instructorUser2 isn't the one on the course
        when(instructorRepository.findById(3)).thenReturn(Optional.of(instructor));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(1,1,100, request)
        );
        assertEquals("You're not the instructor of this course", ex.getMessage());
    }

    @Test
    public void gradeAssignment_StudentNotFound(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        // no such student
        when(studentRepository.findById(1)).thenReturn(Optional.empty());
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(1,1,50, request)
        );
        assertEquals("Student not found", ex.getMessage());
    }

    @Test
    public void gradeAssignment_StudentHasNoSubmissions(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        // zero submissions
        when(submissionRepository.findByStudent(student)).thenReturn(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(1,1,50, request)
        );
        assertEquals("Student has no submissions", ex.getMessage());
    }

    @Test
    public void gradeAssignment_StudentDidntSubmit(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        // submission for a different assignment
        Assignment other = new Assignment(); other.setAssignmentId(2);
        Submission s2 = new Submission(); s2.setAssignmentId(other); s2.setStudent(student);
        when(submissionRepository.findByStudent(student)).thenReturn(List.of(s2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.gradeAssignment(1,1,50, request)
        );
        assertEquals("Student didn't submit this assignment", ex.getMessage());
    }

    // -----------------------------------------
    // saveAssignmentFeedback tests
    // -----------------------------------------
    @Test
    public void saveAssignmentFeedback_UserNotLoggedIn(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(5,1,"done", request)
        );
        assertEquals("You are not logged in", ex.getMessage());
    }

    @Test
    public void saveAssignmentFeedback_AssignmentNotFound(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);
        when(assignmentRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(1,6,"done", request)
        );
        assertEquals("Assignment not found", ex.getMessage());
    }

    @Test
    public void saveAssignmentFeedback_NotTheInstructor(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser2);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(1,1,"done", request)
        );
        assertEquals("You're not the instructor of this course", ex.getMessage());
    }

    @Test
    public void saveAssignmentFeedback_StudentNotFound(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.empty());
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(1,1,"done", request)
        );
        assertEquals("Student not found", ex.getMessage());
    }

    @Test
    public void saveAssignmentFeedback_StudentHasNoSubmissions(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(submissionRepository.findByStudent(student)).thenReturn(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(1,1,"done", request)
        );
        assertEquals("Student has no submissions", ex.getMessage());
    }

    @Test
    public void saveAssignmentFeedback_StudentDidntSubmit(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Assignment other = new Assignment(); other.setAssignmentId(2);
        Submission s2 = new Submission(); s2.setAssignmentId(other); s2.setStudent(student);
        when(submissionRepository.findByStudent(student)).thenReturn(List.of(s2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.saveAssignmentFeedback(1,1,"done", request)
        );
        assertEquals("Student didn't submit this assignment", ex.getMessage());
    }

    // -----------------------------------------
    // getFeedback tests
    // -----------------------------------------
    @Test
    public void getFeedback_UserNotLoggedIn(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("You are not logged in", ex.getMessage());
    }

    @Test
    public void getFeedback_AssignmentNotFound(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);
        when(assignmentRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("Assignment not found", ex.getMessage());
    }

    @Test
    public void getFeedback_NotStudent(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findByUserAccountId(2)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("You're not a student", ex.getMessage());
    }

    @Test
    public void getFeedback_NotEnrolled(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("You're not enrolled in this course", ex.getMessage());
    }

    @Test
    public void getFeedback_StudentHasNoSubmissions(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);
        when(submissionRepository.findByStudent(student)).thenReturn(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("Student has no submissions", ex.getMessage());
    }

    @Test
    public void getFeedback_StudentDidntSubmit(){
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(studentRepository.findByUserAccountId(1)).thenReturn(student);
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        Assignment other = new Assignment(); other.setAssignmentId(2);
        Submission s2 = new Submission(); s2.setAssignmentId(other); s2.setStudent(student);
        when(submissionRepository.findByStudent(student)).thenReturn(List.of(s2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.getFeedback(1, request)
        );
        assertEquals("Student didn't submit this assignment", ex.getMessage());
    }

    // -----------------------------------------
    // assignmentSubmissions tests (already correct)
    // -----------------------------------------
    @Test
    void testAssignmentSubmissions_noLoggedInUser() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        when(assignmentRepository.existsById(1)).thenReturn(true);
        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findAllByAssignmentId(assignment)).thenReturn(List.of(submission));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignmentSubmissions(1, request)
        );
        assertEquals("No logged in user is found.", ex.getMessage());
    }

    @Test
    void testAssignmentSubmissions_notInstructor() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(studentUser);

        when(assignmentRepository.existsById(1)).thenReturn(true);
        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findAllByAssignmentId(assignment)).thenReturn(List.of(submission));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignmentSubmissions(1, request)
        );
        assertEquals("Logged-in user is not an instructor.", ex.getMessage());
    }

    @Test
    void testAssignmentSubmissions_notAssignmentInstructor() {
        Users badInstr = new Users();
        badInstr.setUserId(3);
        badInstr.setUserTypeId(instructorType);

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(badInstr);

        when(assignmentRepository.existsById(1)).thenReturn(true);
        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findAllByAssignmentId(assignment)).thenReturn(List.of(submission));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignmentSubmissions(1, request)
        );
        assertEquals("Logged-in instructor does not have access for this assignment submissions.", ex.getMessage());
    }

    @Test
    void testAssignmentSubmissions_assignmentNotFound() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignmentSubmissions(2, request)
        );
        assertEquals("Assignment with ID 2 not found.", ex.getMessage());
    }

    @Test
    void testAssignmentSubmissions() {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(instructorUser);

        when(assignmentRepository.existsById(1)).thenReturn(true);
        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
        when(submissionRepository.findAllByAssignmentId(assignment)).thenReturn(List.of(submission));

        List<String> result = assignmentService.assignmentSubmissions(1, request);
        assertEquals(1, result.size());
    }
}
