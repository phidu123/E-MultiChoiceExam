package vn.edu.tdtu.exam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.exam.entity.*;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.service.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired AccountService accountService;

    @Autowired
    ResetPasswordService resetPasswordService;
    @Autowired
    StudentSubjectService studentSubjectService;
    @Autowired
    SubjectService subjectService;
    private final JavaMailSender javaMailSender;
    @Autowired
    ExamService examService;

    @Autowired
    public AdminController(StudentSubjectService studentSubjectService, SubjectService subjectService, JavaMailSender javaMailSender) {
        this.studentSubjectService = studentSubjectService;
        this.subjectService = subjectService;
        this.javaMailSender = javaMailSender;
    }
    @GetMapping()
    public String admin() {
        return "admin/home";
    }

// ------------------------- Danh sách sinh viên dự thi + Export file csv ----------------------
    @GetMapping("/list-exam")
    public String examList(Model model, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        List<StudentSubject> studentSubjects = studentSubjectService.findByBanned(false);
        List<Subject> subject = subjectService.findAllSubject();
        model.addAttribute("st", studentSubjects);
        model.addAttribute("subject", subject);
        return "admin/list-exam";
    }
    @GetMapping("/list-exam/filter")
    public String filter(Model model, @RequestParam(required = false) String subject, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("choSubject", subject);
        List<StudentSubject> studentSubjects;

        if ("All".equals(subject) || subject == null) {
            studentSubjects = studentSubjectService.findByBanned(false);
        } else {
            studentSubjects = studentSubjectService.filterBySubject(subject);
        }

        List<Subject> subjectList = subjectService.findAllSubject();
        model.addAttribute("st", studentSubjects);
        model.addAttribute("subject", subjectList);

        return "admin/list-exam";
    }
    @GetMapping("/list-exam/export-csv")
    public void exportCsv(@RequestParam(required = false) String subject, HttpServletResponse response) throws IOException {
        List<StudentSubject> studentSubjects;

        if ("All".equals(subject) || subject == null) {
            studentSubjects = studentSubjectService.findByBanned(false);
        } else {
            studentSubjects = studentSubjectService.filterBySubject(subject);
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=exam_list.csv");

        int index = 1;
        for (StudentSubject studentSubject : studentSubjects) {
            response.getWriter().println(
                    index++ + "," +
                            studentSubject.getStudent().getId() + "," +
                            studentSubject.getStudent().getName() + "," +
                            studentSubject.getSubject().getName()
            );
        }
    }

// ------------------------- Quản lý kế hoạch thi --------------------------
    @GetMapping("/plans-exam")
    public String plansExam(Model model, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        List<Exam> examsPlans = examService.getAllExams();
        model.addAttribute("plans", examsPlans);
        return "admin/plans-exam";
    }

    @GetMapping("/add-plan")
    public String addPlan(HttpSession session){
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        return "admin/add-plan";
    }

    @RequestMapping(value = "/add-plan", method = RequestMethod.POST)
    public String addPlan(Model model, @RequestParam("name") String name, @RequestParam("date") LocalDateTime date, HttpSession session){
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        Exam exam = new Exam(name, date);
        examService.save(exam);
        return "redirect:/admin/plans-exam";

    }
    @GetMapping("/update-plan/{id}")
    public String updatePlan(Model model, HttpServletRequest request, @PathVariable Long id){
        Optional<Exam> optionalExam = examService.findById(id);
        if(optionalExam.isPresent()){
            Exam exam = optionalExam.get();
            model.addAttribute("exam", exam);
            return "admin/update-plan";
        }
        return "admin/error";

    }

    @RequestMapping(value = "/update-plan/{id}", method = RequestMethod.POST)
    public String updatePlan(Model model, @PathVariable Long id,
                             @RequestParam("name") String name, @RequestParam("date") LocalDateTime date,
                             HttpSession session){
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        Optional<Exam> optionalExam = examService.findById(id);
        if(optionalExam.isPresent()){
            Exam exam = optionalExam.get();
            exam.setName(name);
            exam.setExamDate(date);
            examService.save(exam);
            return "redirect:/admin/plans-exam";
        }
        return "admin/error";

    }
// ------------------------- Quản lý yêu cầu khôi phục mật khẩu --------------------------
    @GetMapping("/reset-password")
    public String resetPassword(Model model, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        List<ResetPassword> resetPasswords = resetPasswordService.findByStatus("not");
        model.addAttribute("reset", resetPasswords);
        return "admin/reset-password";
    }

    @PostMapping("/reset-password/send")
    public String sendResetPassword(@RequestParam("email") String email, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        String newPassword = generateRandomPassword();

        sendEmail(email, newPassword);

        resetPasswordService.updatePasswordByEmail(email, newPassword);

        return "redirect:/admin/reset-password";
    }
    private static final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 9; i++) {
            int index = random.nextInt(ALL_CHARS.length());
            password.append(ALL_CHARS.charAt(index));
        }

        return password.toString();
    }

    private void sendEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText("Your new password is: " + newPassword);

        javaMailSender.send(message);
    }

// ------------------------- Quản lý các tài khoản người dùng ---------------------------
    @GetMapping("/user-management")
    public String userManagement(Model model, HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        List<Account> userList = accountRepository.findAll();
        model.addAttribute("userList", userList);
        return "admin/user-management";
    }
    @GetMapping("/register-account")
    public String addUser(HttpSession session) {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        return "admin/register-account";
    }

    @RequestMapping(value = "/register-account", method = RequestMethod.POST)
    public String registerAccount(Model model, HttpSession session,
                                  @RequestParam("image") MultipartFile image,
                                  @RequestParam("name") String name, @RequestParam("email") String email,
                                  @RequestParam("password") String password, @RequestParam("birth") LocalDate birth,
                                  @RequestParam("workplace") String workplace, @RequestParam("role") String role,
                                  @RequestParam("address") String address, @RequestParam("phone") String phone) throws IOException {
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        try {
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());

            String uploadDir = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                String uniqueFileName = generateUniqueFileName(fileName);
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
            String imagePath = "/uploads/" + fileName;
            Account account = new Account(email, password, name, address, workplace, phone, birth, imagePath, true, role);
            accountService.save(account);
            return "redirect:/admin/user-management";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "error";
        }
    }

    private String generateUniqueFileName(String fileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return timestamp + "_" + fileName;
    }
    @RequestMapping(value = "/update-user-status/{id}", method = RequestMethod.POST)
    public String update(Model model, HttpSession session,
                         @PathVariable Long id, @RequestParam("image") MultipartFile image,
                         @RequestParam("name") String name, @RequestParam("email") String email,
                         @RequestParam("password") String password, @RequestParam("birth") LocalDate birth,
                         @RequestParam("workplace") String workplace, @RequestParam("role") String role,
                         @RequestParam("address") String address, @RequestParam("phone") String phone,
                         @RequestParam("active") Boolean active) throws IOException {

        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }

        Optional<Account> optionalAccount = accountService.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setEmail(email);
            account.setPassword(password);
            account.setAddress(address);
            account.setName(name);
            account.setRole(role);
            account.setPhone(phone);
            account.setWorkplace(workplace);
            account.setDoB(birth);
            account.setIsActive(active);

            try {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());

                String uploadDir = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = image.getInputStream()) {
                    String uniqueFileName = generateUniqueFileName(fileName);
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ioe) {
                    throw new IOException("Could not save image file: " + fileName, ioe);
                }

                String imagePath = "/uploads/" + fileName;
                account.setAvatar(imagePath);
                accountService.save(account);

                return "redirect:/admin/user-management";
            } catch (Exception e) {
                model.addAttribute("error", "An error occurred during registration. Please try again.");
                return "error";
            }
        } else {
            return "admin/error";
        }
    }
    @GetMapping("/update-user-status/{id}")
    public String updateAccount(Model model, HttpServletRequest request, @PathVariable Long id, HttpSession session){
        if (session.getAttribute("role") != null && !session.getAttribute("role").equals("admin")) {
            return "redirect:/login";
        }
        Optional<Account> optionalAccount = accountService.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            return "admin/update-account";
        }
        else {
            return "admin/error";
        }
    }
    @GetMapping("/byRole")
    public ResponseEntity<List<Account>> getUsersByRole(@RequestParam String role) {
        List<Account> users = accountService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}
