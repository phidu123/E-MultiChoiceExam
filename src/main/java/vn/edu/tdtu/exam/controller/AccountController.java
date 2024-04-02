package vn.edu.tdtu.exam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.tdtu.exam.dto.ResetPasswordDTO;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Student;
import vn.edu.tdtu.exam.entity.Teacher;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.service.AccountService;
import vn.edu.tdtu.exam.service.StudentService;
import vn.edu.tdtu.exam.service.TeacherService;
import vn.edu.tdtu.exam.utils.TokenManager;

import java.util.List;

@Controller
@RequestMapping("/")
public class AccountController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManager tokenManager;

    @GetMapping()
    public String index(@RequestHeader(value = "Authorization", required = false) String jwt,
                        HttpSession session,
                        Model model) {
        if (jwt == null &&
            session.getAttribute("id") == null &&
            session.getAttribute("jwt") == null) {
            return "redirect:/login";
        }
        Long id = (Long) session.getAttribute("id");
        String jwtToken = (String) session.getAttribute("jwt");

        String role = accountService.getAccount(id).getRole();
        model.addAttribute("jwt", jwtToken);
        model.addAttribute("role", role);
        System.out.println("ROLE: " + role);
        if (role.equals("teacher")) {
            model = getTeacherInformation(model, id);
            return "layouts/home";
        } else if (role.equals("admin")) {
            return "admin/home";
        } else if (role.equals("student")) {
            model = getStudent(model, id);
            return "layouts/home";
        }
        return "login";
    }

    private Model getTeacherInformation(Model model, Long id) {
        Teacher teacher = teacherService.getTeacher(id);
        List<String> educational = List.of(teacher.getEducationalBackground().split(";"));
        model.addAttribute("name", teacher.getName());
        model.addAttribute("email", teacher.getEmail());
        model.addAttribute("phone", teacher.getPhone());
        model.addAttribute("dob", teacher.getDoB());
        model.addAttribute("address", teacher.getAddress());
        model.addAttribute("faculty", teacher.getFaculty());
        model.addAttribute("workplace", teacher.getWorkplace());
        model.addAttribute("position", teacher.getPosition());
        model.addAttribute("degree", teacher.getDegree());
        model.addAttribute("eduBg", educational);
        model.addAttribute("research", teacher.getField());
        return model;
    }
    private Model getStudent(Model model, Long id) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("name", student.getName());
        model.addAttribute("email", student.getEmail());
        model.addAttribute("phone", student.getPhone());
        model.addAttribute("dob", student.getDoB());
        model.addAttribute("address", student.getAddress());
        model.addAttribute("workplace", student.getWorkplace());
        model.addAttribute("major", student.getMajor());
        model.addAttribute("enrollment_year", student.getEnrollment_year());
        return model;
    }

    @GetMapping("/login")
    public String loginGetRequest(HttpSession session) {
        return "login";
    }

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String loginPostRequest(
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            redirectAttributes.addFlashAttribute("flashMessage", "Account disabled");
            redirectAttributes.addFlashAttribute("flashType", "failed");
            return "redirect:/login";
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("flashMessage", "Incorrect email or password");
            redirectAttributes.addFlashAttribute("flashType", "failed");
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }
        Account user = accountService.getUserByEmail(email);
        String jwtToken = tokenManager.generateJwtToken(user);
        System.out.println(jwtToken);
        session.setAttribute("id", user.getId());
        session.setAttribute("jwt", jwtToken);
        session.setAttribute("role", user.getRole());
        return "redirect:/";
    }

    @GetMapping("/password")
    public String getPassword() {
        return "password";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute ResetPasswordDTO resetPasswordDTO, HttpSession httpSession) {
        Long id =(Long) httpSession.getAttribute("id");
        Account account = accountService.findById(id).orElse(null);
        if(account!=null) {
            String oldPassword = resetPasswordDTO.getOldpassword();
            String newPassword = resetPasswordDTO.getNewpassword();
            if(passwordEncoder.matches(oldPassword, account.getPassword())) {
                account.setPassword(passwordEncoder.encode(newPassword));
                accountRepository.save(account);
            }
        }
        return "redirect:/";
    }
}





