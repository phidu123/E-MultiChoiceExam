package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "student")
public class Student extends Account {
    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(name = "major")
    private String major;

    @Column(name = "enrollment_year")
    private Integer enrollment_year;
}
