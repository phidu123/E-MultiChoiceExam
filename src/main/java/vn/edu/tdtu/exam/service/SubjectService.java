package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.dto.AccountDTO;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Subject;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.repository.SubjectRepository;
import vn.edu.tdtu.exam.repository.TeacherSubjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TeacherSubjectRepository teacherSubjectRepository;

    public List<Subject> getSubjectOfTeacher(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            List<Subject> subjectList = subjectRepository.findSubjectsByTeacherId(account.getId());
            return subjectList;
        }
        return null;
    }

    public String getScheduleOfTeacher(Long id) {
        String scheduleList = teacherSubjectRepository.findScheduleIdBySubjectId(id);
        return scheduleList;
    }

    public Integer getExamPaperOfSubjectQuantity(Long id) {
        return subjectRepository.findExamPaperQuantity(id);
    }

    public Subject getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId).orElse(null);
    }
    public List<Subject> findAllSubject(){
        return subjectRepository.findAll();
    }
    public Long findSubjectIdByName(String subjectName) {
        Subject subject = subjectRepository.findByName(subjectName);
        return (subject != null) ? subject.getId() : null;
    }

    public Integer findStudentInSubject(Long subjectID) {
        return subjectRepository.countStudent(subjectID);
    }
}
