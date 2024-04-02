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
import vn.edu.tdtu.exam.entity.Exam;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.Subject;
import vn.edu.tdtu.exam.service.AccountService;
import vn.edu.tdtu.exam.service.ExamPaperService;
import vn.edu.tdtu.exam.service.ExamService;
import vn.edu.tdtu.exam.service.SubjectService;
import vn.edu.tdtu.exam.utils.MediaTypeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/paper")
public class ExamPaperController {

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

    @GetMapping("/detail")
    public String showPaperDetail(@RequestParam Long id, Model model, HttpSession session) {
        if (!session.getAttribute("role").equals("teacher")) {
            return "login";
        }

        Long userId = (Long) session.getAttribute("id");
        ExamPaper test = testService.getTestsById(id);
        model.addAttribute("test", test);

        List<Exam> exams = examService.getAllExams();
        model.addAttribute("category", exams);

        Subject subject = subjectService.getSubjectById(test.getSubject().getId());
        model.addAttribute("subject", subject);

        model.addAttribute("isAuthor", testService.checkAuthor(test.getId(), userId));
//        System.out.println(userId + " " + test.getTeacher().getId());
//        System.out.println(testService.checkAuthor(userId, test.getTeacher().getId()));
        return "teacher/exam-paper-detail";
    }

    @GetMapping("/add/{subjectId}")
    public String addNewPaper(@PathVariable Long subjectId, Model model) {
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Exam> exams = examService.getAllExams();

        model.addAttribute("subject", subject);
        model.addAttribute("category", exams);
        return "teacher/exam-paper-form";
    }

    @PostMapping("/add")
    public String submitNewPaper(
            @ModelAttribute ExamPaperDTO form,
            @RequestParam("exampaper") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long teacherId = (Long) session.getAttribute("id");
        if (!session.getAttribute("role").equals("teacher")) {
            return "/login";
        }
        Boolean result = testService.addTest(teacherId, form, file);

        if (result) {
            redirectAttributes.addFlashAttribute("flashMessage", "Exam paper has been added successfully");
            redirectAttributes.addFlashAttribute("flashType", "success");
        } else {
            redirectAttributes.addFlashAttribute("flashMessage", "Error adding the exam paper");
            redirectAttributes.addFlashAttribute("flashType", "failed");
        }

        // handle file upload
        return "redirect:/bank/exam?s=" + form.getSubject();
    }

    @PostMapping("/update")
    public String updatePaper(
            @RequestParam Long id,
            @ModelAttribute ExamPaperDTO form,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!session.getAttribute("role").equals("teacher")) {
            return "login";
        }
        Boolean result = testService.updateTest(id, form);
        if (result) {
            redirectAttributes.addFlashAttribute("flashMessage", "Exam paper has been updated successfully");
            redirectAttributes.addFlashAttribute("flashType", "success");
        } else {
            redirectAttributes.addFlashAttribute("flashMessage", "Error updating the exam paper");
            redirectAttributes.addFlashAttribute("flashType", "failed");
        }

        // handle file upload
        return "redirect:/bank/exam?s=" + form.getSubject();
    }

    @PostMapping("/delete")
    public String deletePaper(
            @RequestParam("id") Long id, @ModelAttribute("subject") String subject,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!session.getAttribute("role").equals("teacher")) {
            return "/login";
        }

        Long userId = (Long) session.getAttribute("id");
        Boolean isAuthor = testService.checkAuthor(id, userId);
        if (!isAuthor) {
            redirectAttributes.addFlashAttribute("flashMessage", "You cannot delete this test because you are not the author");
            redirectAttributes.addFlashAttribute("flashType", "failed");
            return "redirect:/bank/exam?s=" + subject;
        }

        boolean deletionSuccessful = testService.deletePaper(id);

        if (deletionSuccessful) {
            redirectAttributes.addFlashAttribute("flashMessage", "Exam paper deleted successfully");
            redirectAttributes.addFlashAttribute("flashType", "success");
        } else {
            redirectAttributes.addFlashAttribute("flashMessage", "Error deleting exam paper");
            redirectAttributes.addFlashAttribute("flashType", "failed");
        }
        return "redirect:/bank/exam?s=" + subject;
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String fileName) throws IOException {

        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);

        File file = new File("upload" + "/" + fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString());

        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=UTF-8''" + encodedFileName)
                // Content-Type
                .contentType(mediaType)
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }


}






