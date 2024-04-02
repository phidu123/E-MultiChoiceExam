package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exam_result")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "exampaper_id")
    private ExamPaper examPaper;

    @Column(name = "score")
    private Double score;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "taken_time")
    private Integer takenTime;

    @Column(name = "attempt")
    private Integer attempt;

    public ExamResult (Student student, ExamPaper examPaper, Double score, LocalDateTime startTime, Integer takenTime, Integer attempt){
        this.student = student;
        this.examPaper = examPaper;
        this.score = score;
        this.startTime = startTime;
        this.takenTime = takenTime;
        this.attempt = attempt;
    }
}
