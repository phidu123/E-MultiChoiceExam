package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.entity.ResetPassword;
import vn.edu.tdtu.exam.repository.ResetPasswordRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ResetPasswordService {
    private final ResetPasswordRepository resetPasswordRepository;

    @Autowired
    public ResetPasswordService(ResetPasswordRepository resetPasswordRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
    }
    public List<ResetPassword> findAll() {
        return resetPasswordRepository.findAll();
    }
    public void updatePasswordByEmail(String email, String newPassword) {
        Optional<ResetPassword> resetPasswordOptional = resetPasswordRepository.findByAccountEmail(email);

        resetPasswordOptional.ifPresent(resetPassword -> {
            resetPassword.getAccount().setPassword(newPassword);
            resetPassword.setStatus("done");
            resetPasswordRepository.save(resetPassword);
        });
    }
    public List<ResetPassword> findByStatus(String status) {
        return resetPasswordRepository.findByStatus(status);
    }
}
