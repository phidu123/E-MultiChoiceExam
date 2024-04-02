package vn.edu.tdtu.exam.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.tdtu.exam.dto.ExamPaperDTO;
import vn.edu.tdtu.exam.dto.StatisticDTO;
import vn.edu.tdtu.exam.entity.Exam;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.ExamResult;
import vn.edu.tdtu.exam.entity.Subject;
import vn.edu.tdtu.exam.service.*;
import vn.edu.tdtu.exam.utils.MediaTypeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private AccountService accountService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamPaperService testService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamResultService examResultService;

    @GetMapping()
    public String showStatistic(HttpSession session, Model model) {
        if (!session.getAttribute("role").equals("teacher")) {
            return "login";
        }

        Long userId = (Long) session.getAttribute("id");

        List<Subject> subjectList = subjectService.getSubjectOfTeacher(userId);
        List<Exam> exams = new ArrayList<>();
        subjectList.forEach(subject -> {
            exams.addAll(testService.getExamByTeacherAndSubject(userId, subject.getId()));
        });
        model.addAttribute("exams", exams);
        model.addAttribute("subjects", subjectList);
        model.addAttribute("teacher", userId);
        return "teacher/statistic";
    }

    @PostMapping()
    public ResponseEntity<?> handleStatisticRequest(@RequestBody StatisticDTO request) {
        // Perform logic based on the received data
        Long subject = request.getSubject();
        Long exam = request.getExam();
        Long teacher = request.getTeacher();

        Long testId = testService.getTestForStatistic(subject, exam, teacher);

        List<Double> scores = examResultService.getScoresTestId(testId);

        // Return a response (this is just an example)
        return ResponseEntity.ok(scores);
    }


}






