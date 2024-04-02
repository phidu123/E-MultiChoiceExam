package vn.edu.tdtu.exam.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Exam;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.Subject;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Exam getExamById(Long category);

}
