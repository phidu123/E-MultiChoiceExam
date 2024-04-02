package vn.edu.tdtu.exam.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.exam.entity.*;
import vn.edu.tdtu.exam.service.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/bank")
public class BankController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamPaperService testService;

    @Autowired
    private ExamService examService;

    @GetMapping
    public String showMySubject(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        String role = accountService.getAccount(id).getRole();
        if (!role.equals("teacher")) {
            return "redirect:/login";
        }
        List<Subject> subjects = subjectService.getSubjectOfTeacher((Long) session.getAttribute("id"));
        if (subjects == null) {
            model.addAttribute("errMsg", "No subjects found");
            return "teacher/bank-exam";
        } else {
            List<Integer> quantity = new ArrayList<>();
            subjects.forEach(subject -> {
                quantity.add(
                        testService.getTestQuantityBySubject(subject.getId())
                );
            });
            model.addAttribute("subjects", subjects);
            model.addAttribute("quantity", quantity);
            return "teacher/bank-exam";
        }
    }

    @GetMapping("/exam")
    public String showExam(
            @RequestParam(name = "s") Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            Model model) {
        if (subjectId != null) {
            Page<ExamPaper> tests = testService.getTestsBySubject(subjectId, page, size);

            // set attribute
            List<Teacher> teachers = new ArrayList<>();
            List<Exam> exams = new ArrayList<>();
            tests.forEach(test -> {
                teachers.add(
                        accountService.getTeacher(test.getTeacher().getId())
                );
                exams.add(
                        examService.getExamById(test.getExam().getId())
                );
            });

            // return attributes to view
            model.addAttribute("isExam", true);
            model.addAttribute("quantity", tests.getTotalElements());
            model.addAttribute("subject", subjectId);
            if (tests.getTotalElements() == 0) {
                model.addAttribute("errMsg", "This subject does not have any exams");
                return "teacher/bank-exam";
            }
            model.addAttribute("tests", tests);
            model.addAttribute("teachers", teachers);
            model.addAttribute("exams", exams);
            model.addAttribute("currentPage", tests.getNumber());
            model.addAttribute("totalPages", tests.getTotalPages());
            model.addAttribute("size", size);
            model.addAttribute("uniqueTeachers", getUniqueTeachers(teachers));
            model.addAttribute("uniqueExams", getUniqueExams(exams));
            return "teacher/bank-exam";
        }
        return "500";
    }

    private Set<Exam> getUniqueExams(List<Exam> exams) {
        Set<String> uniqueExamNames = new HashSet<>();
        Set<Exam> uniqueExams = new HashSet<>();

        for (Exam exam : exams) {
            if (uniqueExamNames.add(exam.getName())) {
                uniqueExams.add(exam);
            }
        }

        return uniqueExams;
    }

    private Set<Teacher> getUniqueTeachers(List<Teacher> teachers) {
        Set<String> uniqueTeacherNames = new HashSet<>();
        Set<Teacher> uniqueTeachers = new HashSet<>();

        for (Teacher teacher : teachers) {
            if (uniqueTeacherNames.add(teacher.getName())) {
                uniqueTeachers.add(teacher);
            }
        }

        return uniqueTeachers;
    }
}






