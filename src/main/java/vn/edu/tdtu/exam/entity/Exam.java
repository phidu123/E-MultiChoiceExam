package vn.edu.tdtu.exam.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate;
    public Exam(String name, LocalDateTime examDate){
        this.name = name;
        this.examDate = examDate;
    }
    public Exam(){}
}
