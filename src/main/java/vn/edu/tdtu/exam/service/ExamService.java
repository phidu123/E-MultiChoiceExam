package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Exam;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.Subject;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.repository.ExamRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("ExamService")
public class ExamService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ExamRepository examRepository;

    public Exam getExamById(Long id) {
        Optional<Exam> optionalExam = examRepository.findById(id);
        if (optionalExam.isPresent()) {
            return optionalExam.get();
        }
        return null;
    }

    public List<Exam> getAllExams() {
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "examDate");
        return examRepository.findAll(sortByDateDesc);
    }

    public List<Exam> getAll(){
        return examRepository.findAll();
    }
    public Exam save(Exam exam){
        return examRepository.save(exam);
    }
    public Optional<Exam> findById(Long id) {
        return examRepository.findById(id);
    }

    public String getDayOfWeek(Exam exam){
        return exam.getExamDate().getDayOfWeek().name();
    }
    public String getTime(Exam exam){
        LocalDateTime date = exam.getExamDate();
        return date.getHour() + "";
    }
    public String getDate(Exam exam){
        return exam.getExamDate().getDayOfWeek().name();
    }

}
