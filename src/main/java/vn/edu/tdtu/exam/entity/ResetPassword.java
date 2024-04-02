package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "status", nullable = false)
    private String status;

    public ResetPassword(Account account, LocalDateTime time, String status) {
        this.account = account;
        this.time = time;
        this.status = status;
    }
}
