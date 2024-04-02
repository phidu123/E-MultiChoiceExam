package vn.edu.tdtu.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String studentId;
    private String studentName;
    private String major;
    private String email;
    private String password;
    private Integer enrollment_year;
    private String address;
    private String phone;
    private String workplace;
    private LocalDate DoB;
    private String avatar;
}
