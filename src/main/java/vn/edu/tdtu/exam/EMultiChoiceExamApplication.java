package vn.edu.tdtu.exam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.service.AccountService;

@SpringBootApplication
@Component
public class EMultiChoiceExamApplication implements CommandLineRunner {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(EMultiChoiceExamApplication.class, args);
	}

	private void createAdminAccount() {
		if (accountService.getUserByEmail("admin") == null) {
			Account account = new Account();
			account.setEmail("admin");
			account.setPassword(passwordEncoder.encode("admin"));
			account.setRole("admin");
			accountRepository.save(account);
		}
//		String password = passwordEncoder.encode("123");
//		System.out.println(passwordEncoder.matches("123", password));
	}

	@Override
	public void run(String... args) throws Exception {
		createAdminAccount();
		System.out.println("http://localhost:8080/");
	}
}
