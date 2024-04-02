package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "exampaper_id")
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Report(ExamPaper examPaper, Student student, String description){
        this.examPaper = examPaper;
        this.student = student;
        this.description = description;
        this.status = false;
    }
}

