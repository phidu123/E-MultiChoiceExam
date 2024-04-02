package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.dto.AccountDTO;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Teacher;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.repository.TeacherRepository;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public Account add(AccountDTO accountDTO) {
//        Account account = new Account();
//        account.setEmail(accountDTO.getEmail());
//        account.setPassword(accountDTO.getPassword());
//        return accountRepository.save(account);
        return null;
    }

    public Teacher getTeacher(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

}
