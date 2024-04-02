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
import java.util.List;

@Controller
@RequestMapping("/class")
public class ClassesController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentSubjectService studentSubjectService;

    @GetMapping
    public String showMyClasses(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!role.equals("teacher")) {
            return "/login";
        }

        List<Subject> subjects = subjectService.getSubjectOfTeacher((Long) session.getAttribute("id"));
        List<String> schedules = new ArrayList<>();
        List<Integer> students = new ArrayList<>();

        subjects.forEach(subject -> {
            String schedule = subjectService.getScheduleOfTeacher(subject.getId());
            Integer student = subjectService.findStudentInSubject((subject.getId()));

            schedules.add(schedule);
            students.add(student);

        });

        model.addAttribute("listClass", subjects);
        model.addAttribute("schedule", schedules);
        model.addAttribute("numberOfStudent", students);

        return "teacher/my-classes";
    }

    @GetMapping("/listClass")
        public String classList(@RequestParam(name = "s") Long subjectId, HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!role.equals("teacher")) {
            return "/login";
        }
        if (subjectId != null) {
            List<Student> students = studentService.getListStudent(subjectId);
            List<Boolean> checkBanned = new ArrayList<>();

            for (Student student : students) {
                checkBanned.add(studentSubjectService.showBanned(student.getId(), subjectId));
            }

//            System.out.println(students.size());
            model.addAttribute("listStudent", students);
            model.addAttribute("subject", subjectId);
            model.addAttribute("showBanned", checkBanned);
            return "teacher/my-classes";
        }
        return "404";
    }

    @PostMapping (value = "/banned", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String bannedStudent(@RequestParam(name = "id") Long studentID, @ModelAttribute("subject") Long subject, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!role.equals("teacher")) {
            return "/login";
        }
        if (studentID != null) {
            studentSubjectService.bannedStudent(studentID, subject, true);
            return "redirect:/class/listClass?s=" + subject;
        }
        return "404";
    }

    @PostMapping (value = "/unbanned", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String unBannedStudent(@RequestParam(name = "id") Long studentID, @ModelAttribute("subject") Long subject, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!role.equals("teacher")) {
            return "/login";
        }
        if (studentID != null) {
            studentSubjectService.bannedStudent(studentID, subject, false);
            return "redirect:/class/listClass?s=" + subject;
        }
        return "404";
    }
}






