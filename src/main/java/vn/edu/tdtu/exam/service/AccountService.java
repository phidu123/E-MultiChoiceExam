package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.dto.AccountDTO;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Teacher;
import vn.edu.tdtu.exam.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Account add(AccountDTO accountDTO) {
//        Account account = new Account();
//        account.setEmail(accountDTO.getEmail());
//        account.setPassword(accountDTO.getPassword());
//        return accountRepository.save(account);
        return null;
    }

    public Boolean find(Account account) {
        Account foundAccount = accountRepository.findByEmailAndPassword(account.getEmail(), account.getPassword());
        return foundAccount != null;
    }

    public Account getAccount(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            return optionalAccount.get();
        }
        return null;
    }

    public Teacher getTeacher(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account instanceof Teacher) {
                return (Teacher) account;
            }
        }
        return null;
    }

    public String getTeacherNameById(Long teacherId) {
        Optional<Account> teacherOptional = accountRepository.findById(teacherId);

        if (teacherOptional.isPresent()) {
            Account teacher = teacherOptional.get();
            return teacher.getName();
        } else {
            return null;
        }
    }
    public Optional<Account> findById(Long userId) {
        return accountRepository.findById(userId);
    }
    public Account save(Account account){
        return accountRepository.save(account);
    }
    public List<Account> findAll(){
        return accountRepository.findAll();
    }
    public List<Account> getUsersByRole(String role) {
        return accountRepository.findByRole(role);
    }

    public Account getAccountByEmailAndPassword(String email, String password) {
        return accountRepository.findByEmailAndPassword(email, password);
    }
    public Account getUserByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = accountRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
