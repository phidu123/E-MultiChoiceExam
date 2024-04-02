package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "students_exam")
public class StudentExam {
    @Embeddable
    @Data
    public static class ExamStudentId implements Serializable {
        @Column(name = "exam_id")
        private Long examId;

        @Column(name = "student_id")
        private Long studentId;
    }

    @EmbeddedId
    private ExamStudentId id;

    @ManyToOne
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;
}
