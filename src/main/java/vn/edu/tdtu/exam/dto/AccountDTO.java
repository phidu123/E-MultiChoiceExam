package vn.edu.tdtu.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String email;
    private String password;
    private Boolean isActive;
    private String role;
    private String address;
    private String workplace;
    private String phone;
    private LocalDate DoB;
    private String avatar;
}
