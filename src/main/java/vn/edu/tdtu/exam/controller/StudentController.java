package vn.edu.tdtu.exam.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.tdtu.exam.dto.StudentDTO;
import vn.edu.tdtu.exam.entity.*;
import vn.edu.tdtu.exam.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ExamService examService;
    @Autowired
    private ExamPaperService examPaperService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ExamResultService examResultService;
    @Autowired
    private StudentSubjectService studentSubjectService;

    @GetMapping()
    public String home (HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("id");

        Student student = studentService.getStudentById(id);
        model.addAttribute("name", student.getName());
        model.addAttribute("email", student.getEmail());
        model.addAttribute("phone", student.getPhone());
        model.addAttribute("dob", student.getDoB());
        model.addAttribute("address", student.getAddress());
        model.addAttribute("workplace", student.getWorkplace());
        model.addAttribute("major", student.getMajor());
        model.addAttribute("enrollment_year", student.getEnrollment_year());

        return "layouts/home";
    }

    @GetMapping("/exam_list")
    public String getExamList(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        Long examPaperId = (Long)session.getAttribute("examPaperId");

        Student student = studentService.getStudentById(id);
        ExamPaper examPaper = examPaperService.getTestsById(examPaperId);

        model.addAttribute("examPaper", examPaper);
        return "student/exam_list";
    }
    @GetMapping("/exam_enroll")
    public String tokenEnroll(Model model, HttpSession session){

        String jwtToken = (String) session.getAttribute("jwt");
        model.addAttribute("token", jwtToken);
        return "student/exam_enroll";
    }

    @PostMapping(value = "/exam_enroll", consumes = "application/x-www-form-urlencoded")
    public String joinExam(@RequestParam String token,  HttpSession session, Model model){
        session.removeAttribute("status");

        ExamPaper examPaper = examPaperService.getTestByAccessToken(token);
        Long studentId = (Long)session.getAttribute("id");
        Student student = studentService.getStudentById(studentId);

        //Check Access Token
        if(examPaper == null){
            model.addAttribute("flashMessage", "Wrong access token!");
            model.addAttribute("flashType", "failed");
            return "student/exam_enroll";
        }
        //---------------------

        //Check if student is banned from class subject
        StudentSubject studentSubject = studentSubjectService.getStudentSubjectByStudentAndSubject(student, examPaper.getSubject());


        if(studentSubject == null || studentSubject.getBanned() || !examPaper.getIsActive()) {
            model.addAttribute("flashMessage", "Sorry, you are not allowed to enroll the test.");
            model.addAttribute("flashType", "failed");
            return "student/exam_enroll";
        }
        session.setAttribute("examPaperId", examPaper.getId());

        return "redirect:/student/exam_list";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("id");
        Student student = studentService.getStudentById(id);
        List<Report> reports = reportService.getAllStudentReport(student);
        model.addAttribute("reports", reports);
        return "student/report";
    }
    @GetMapping("/add_report/{id}")
    public String report(HttpSession session, Model model, @PathVariable Long id){
        ExamPaper examPaper = examPaperService.getTestsById(id);
        model.addAttribute("examPaperId", examPaper.getId());
        model.addAttribute("examTitle", examPaper.getTitle());
        model.addAttribute("subjectName", examPaper.getExam().getName());
        return "student/report_form";
    }

    @PostMapping("/add_report/{id}")
    public String createReport(@PathVariable Long id, String description, HttpSession session){
        Long studentId = (Long)session.getAttribute("id");
        ExamPaper examPaper = examPaperService.getTestsById(id);
        Student student = studentService.getStudentById(studentId);
        Report report = reportService.add(new Report(examPaper, student, description));
        return "redirect:/student/reports";
    }

    @GetMapping("/results")
    public String result(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        Student student = studentService.getStudentById(id);

        HashMap<ExamResult, ExamPaper> results = new HashMap<>();
        List<ExamResult> examResults = examResultService.getAllStudentExamResult(student);
        for(ExamResult examResult : examResults){
            ExamPaper examPaper = examPaperService.getTestsById(examResult.getExamPaper().getId());
            if(examPaper.getShowScore() == true)
                results.put(examResult, examPaper);
        }
        model.addAttribute("results",results);
        return "student/result";
    }

    @GetMapping("/schedule")
    public String schedule(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("id");
        Student student = studentService.getStudentById(id);

        //---------------------------Lịch thi riêng của sv----------------------------
//        //Lấy list các môn học mà sv tham gia
//        List<Exam> exams = new ArrayList<>();
//        List<ExamPaper> examPaperList = new ArrayList<>();
//        List<StudentSubject> subjects = studentSubjectService.getStudentSubjectByStudent(student);
//        for(StudentSubject ss : subjects){
//            // Lấy các bài kiểm tra của môn học mà sv tham gia
//            List<ExamPaper> examPapers = examPaperService.getAllTestBySubject(ss.getSubject());
//            for(ExamPaper examPaper : examPapers){
//                Exam exam = examPaper.getExam();
//                if(!exams.contains(exam)){
//                    exams.add(exam);
//                    examPaperList.add(examPaper);
//                }
//            }
//        }
//        model.addAttribute("exams", examPaperList);

        //--------------------------------------------------------------------

        //---------------------------Lịch thi chung----------------------------
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        //--------------------------------------------------------------------

        return "student/schedule";
    }

    @GetMapping("/update_info")
    public String getInfo(HttpSession session, Model model){
        Long id = (Long) session.getAttribute("id");

        Student student = studentService.getStudentById(id);
        model.addAttribute("name", student.getName());
        model.addAttribute("email", student.getEmail());
        model.addAttribute("phone", student.getPhone());
        model.addAttribute("dob", student.getDoB());
        model.addAttribute("address", student.getAddress());
        model.addAttribute("workplace", student.getWorkplace());
        model.addAttribute("major", student.getMajor());
        model.addAttribute("enrollment_year", student.getEnrollment_year());

        return "student/info_form";
    }
    @PostMapping("/update_info")
    public String updateInfo(HttpSession session, @ModelAttribute StudentDTO studentDTO){
        Long id = (Long)session.getAttribute("id");
        System.out.println(studentDTO);

        StudentDTO student = studentService.updateStudent(id, studentDTO);
        System.out.println(student);
        return "redirect:/";
    }
}
