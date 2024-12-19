package com.LMS.Learning_Management_System.service;


import com.LMS.Learning_Management_System.dto.GradingDto;
import com.LMS.Learning_Management_System.dto.QuestionDto;
import com.LMS.Learning_Management_System.dto.QuizDto;
import com.LMS.Learning_Management_System.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.LMS.Learning_Management_System.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Example;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper ;
    private final StudentRepository studentRepository;
    private final GradingRepository gradingRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final EnrollmentRepository enrollmentRepository;

    List<Question> quizQuestions = new ArrayList<>();
    List<Answer> quizAnswers = new ArrayList<>();
    List<Question>questionBank= new ArrayList<>();
    public QuizService(QuizRepository quizRepository, CourseRepository courseRepository, QuestionRepository questionRepository, ObjectMapper objectMapper, StudentRepository studentRepository, GradingRepository gradingRepository, QuestionTypeRepository questionTypeRepository, EnrollmentRepository enrollmentRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
        this.studentRepository = studentRepository;
        this.gradingRepository = gradingRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.enrollmentRepository = enrollmentRepository;
    }


    public void Create(Integer course_id , int type_id ) throws Exception {  // return type ? { list of questions or Quiz }
        /*
        * get the desired course and type of quiz
        * generate 5 random questions based on the course ID
        * store their answers
        * create the quiz with its ID as title
        * map the quiz id to the questions' quiz id
        *
        * CHECKING : number of generated questions >= 5 , the course and type exists
        * */

        Course course= courseRepository.findById(course_id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        if(type_id>3 || type_id<1) throw new Exception("No such type\n");
        List<Quiz> quizzes =  quizRepository.findAll();
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setTitle("quiz"+quizzes.size()+1);
        quiz.setQuestionCount(5);
        quiz.setRandomized(true);
        quiz.setCreationDate(new Date());

        generateQuestions(quiz,type_id, course);
        quizRepository.save(quiz);
    }

    public List<QuestionDto> getQuizQuestions(int id)
    {
        quizRepository.findById(id).orElseThrow(()->new EntityNotFoundException("No such quiz"));
        quizQuestions = questionRepository.findQuestionsByQuizId(id);
        List<QuestionDto> questions =new ArrayList<>();
        for (Question q : quizQuestions) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setOptions(q.getOptions());
            questionDto.setType(q.getQuestionType().getTypeId());
            questionDto.setQuestion_text(q.getQuestionText());
            questionDto.setCorrect_answer(q.getCorrectAnswer());
            questionDto.setCourse_id(q.getCourseId().getCourseId());
            questionDto.setQuestion_id(q.getQuestionId());
            questions.add(questionDto);
        }
        return questions;
    }

    public String getType(int typeID)
    {
        if(typeID==1) return "MCQ";
        else if(typeID==2) return "True/False";
        else return "Short Answer" ;
    }

    public void addQuestion(QuestionDto questionDto) throws Exception {
        Optional<Question> optQuestion = questionRepository.findById(questionDto.getQuestion_id());
        if(optQuestion.isPresent()) throw new Exception("question already exists");
        Question question = new Question();
        Course course =courseRepository.findById(questionDto.getCourse_id())  // check course
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + questionDto.getCourse_id()));

        Question temp = new Question();  // check duplication
        temp.setQuestionText(questionDto.getQuestion_text());
        Example<Question> example = Example.of(temp);
        if(!questionRepository.findAll(example).isEmpty())throw new Exception("Question with this description Already exists!");

        question.setQuestionText(questionDto.getQuestion_text());
        // Handle QuestionType
        QuestionType questionType = questionTypeRepository.findById(questionDto.getType())
                .orElseThrow(() -> new EntityNotFoundException("No such QuestionType"+questionDto.getType()));
        question.setQuestionType(questionType);
        try {
            // Convert List<String> to JSON string
            String optionsAsString = objectMapper.writeValueAsString(questionDto.getOptions());
            question.setOptions(optionsAsString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert options to JSON", e);
        }
        question.setCourseId(course);
        question.setCorrectAnswer(questionDto.getCorrect_answer());
        questionRepository.save(question);

    }

    public void generateQuestions(Quiz quiz,int questionType, Course course_id) throws Exception {

        List<Question> allQuestions = questionRepository
                .findQuestionsByCourseIdAndQuestionType(course_id.getCourseId(),questionType);  // get all questions with same type
        if(allQuestions.size()< 5) throw new Exception("No enough Questions to create quiz!\n");
        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();  // To track selected indices
        int count = 0;
        while (count < 5) {
            int randomNumber = random.nextInt(allQuestions.size());

            if (!selectedIndices.contains(randomNumber)) {
                selectedIndices.add(randomNumber);
                Question selectedQuestion = allQuestions.get(randomNumber);
                selectedQuestion.setQuiz(quiz);
                count++;
            }
        }
    }

    public QuizDto getQuizByID (int id, HttpServletRequest request) {
//        Student loggedInStudent = (Student) request.getSession().getAttribute("user");
//        if (loggedInStudent == null) {
//            throw new IllegalArgumentException("No user is logged in.");
//        }
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No quiz found with the given ID: " + id));
//        Course course = quiz.getCourse();
//        Enrollment enrollment = new Enrollment();
//        enrollment.setCourse(course);
//        enrollment.setStudent(loggedInStudent);
        //Example<Enrollment> example = Example.of(enrollment);
        //if(enrollmentRepository.findAll(example).isEmpty()) throw new Exception("You are not Authorized to enter this quiz!");

        return new QuizDto(
                quiz.getQuizId(),
                quiz.getTitle(),
                quiz.getCreationDate()
                //getQuizQuestions(quiz)
        );
    }


    public void createQuestionBank(int course_id, List<QuestionDto> questions) {
        // Fetch the course
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new EntityNotFoundException("No such Course"));

        Question question1 = new Question();
        question1.setCourseId(course);
        Example<Question>example= Example.of(question1);
        // Load existing questions
        List<Question> questionBank = questionRepository.findAll(example);

        // Process questions
        for (QuestionDto dto : questions) {
            Question question = questionRepository.findById(dto.getQuestion_id())
                    .orElse(new Question()); // Find or create a new question

            question.setQuestionText(dto.getQuestion_text());
            try {
                // Convert List<String> to JSON string
                String optionsAsString = objectMapper.writeValueAsString(dto.getOptions());
                question.setOptions(optionsAsString);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert options to JSON", e);
            }
            question.setCorrectAnswer(dto.getCorrect_answer());
            question.setCourseId(course);

            // Handle QuestionType
            QuestionType questionType = questionTypeRepository.findById(dto.getType())
                    .orElseThrow(() -> new EntityNotFoundException("No such QuestionType"+dto.getType()));
            question.setQuestionType(questionType);

            // Save or update the question
            questionRepository.save(question);
        }
    }


    public QuizDto getQuestionBank(int course_id) throws Exception {
        Course course = new Course();
        course.setCourseId(course_id);
        Question question = new Question();
        question.setCourseId(course);
        Example<Question> example = Example.of(question);

        QuizDto quizDto = new QuizDto();
        questionBank = questionRepository.findQuestionsByCourseId(course_id);
        if(questionBank.isEmpty()) throw new Exception("this course doesn't have any!");
        List<QuestionDto> questionDtos = new ArrayList<>();
        for (int i = 0; i < questionBank.size(); i++) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setQuestion_id(questionBank.get(i).getQuestionId());
            questionDto.setCorrect_answer(questionBank.get(i).getCorrectAnswer());
            questionDto.setQuestion_text(questionBank.get(i).getQuestionText());
            questionDto.setType(questionBank.get(i).getQuestionType().getTypeId());
            questionDto.setCourse_id(questionBank.get(i).getCourseId().getCourseId());
            questionDto.setOptions(questionBank.get(i).getOptions());
            questionDtos.add(questionDto);
        }
        quizDto.setQuestionList(questionDtos);
        return quizDto;
    }

    public List<Question> getQuizQuestions(Quiz quiz_id)
    {
        Question question = new Question();
        question.setQuiz(quiz_id);
        Example<Question> example = Example.of(question);
        return questionRepository.findAll(example);
    }

    public void saveQuestion(Question question)
    {
        Question q = new Question();
        q.setQuestionId(question.getQuestionId());
        q.setQuestionText(question.getQuestionText());
        q.setCourseId(question.getCourseId());
        q.setQuiz(question.getQuiz());
        q.setQuestionType(question.getQuestionType());
        q.setOptions(question.getOptions());
        q.setCorrectAnswer(question.getCorrectAnswer());
        questionRepository.save(q);
    }

    // grade quiz
    public void gradeQuiz(GradingDto gradingDto)
    {
        Optional<Quiz> optionalQuiz= Optional.ofNullable(quizRepository.findById(gradingDto.getQuiz_id())
                .orElseThrow(() -> new EntityNotFoundException("No such Quiz")));
        Quiz quiz = optionalQuiz.get();
        Optional<Student> optionalStudent= Optional.ofNullable(studentRepository.findById(gradingDto.getStudent_id())
                .orElseThrow(() -> new EntityNotFoundException("No such student")));
        Student student = optionalStudent.get();
          // get questions with the quiz id
        List<Question>gradedQuestions=questionRepository.findQuestionsByQuizId(gradingDto.getQuiz_id());
        List<String> answersList = gradingDto.getAnswers();
        int grade=0;
        for (int i = 0; i < gradedQuestions.size(); i++) {
            for (int j = 0; j < gradedQuestions.size(); j++) {
                if(Objects.equals(gradedQuestions.get(i).getCorrectAnswer(), answersList.get(j)))
                {
                    grade++;
                    break;
                }
            }
        }

        Grading grading = new Grading();
        grading.setGrade(grade);
        grading.setQuiz_id(quiz);
        grading.setStudent_id(student);
        gradingRepository.save(grading);

    }

    // return quiz feedback { grade }
    public int quizFeedback(int quiz_id, int student_id) throws Exception {
        Optional<Quiz> optionalQuiz= Optional.ofNullable(quizRepository.findById(quiz_id)
                .orElseThrow(() -> new EntityNotFoundException("No such Quiz")));

        Optional<Student> optionalStudent= Optional.ofNullable(studentRepository.findById(student_id)
                .orElseThrow(() -> new EntityNotFoundException("No such student")));

        int grade = gradingRepository.findGradeByQuizAndStudentID(quiz_id,student_id);
        if(grade ==-1) throw new Exception("Quiz haven't been graded yet");
        return grade;

    }

    //return all grades by a given quiz
    public List<Pair<Integer,Integer>> getQuizGrades(int quiz_id)
    {
        Optional<Quiz> optionalQuiz= Optional.ofNullable(quizRepository.findById(quiz_id)
                .orElseThrow(() -> new EntityNotFoundException("No such Quiz")));
        List<Integer>students = gradingRepository.findStudentByQuiz(quiz_id);
        List<Integer>grades = gradingRepository.findGradeByQuizId(quiz_id);
        List<Pair<Integer, Integer>> pairedList = new ArrayList<>();
        int size = Math.min(students.size(), grades.size());

        for (int i = 0; i < size; i++) {
            pairedList.add(Pair.of(students.get(i), grades.get(i)));
        }

        return pairedList;

    }

}
