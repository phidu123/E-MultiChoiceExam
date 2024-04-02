package vn.edu.tdtu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.ExamResult;
import vn.edu.tdtu.exam.entity.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    List<ExamResult> findAllExamResultByStudent (Student student);
    Optional<ExamResult> findExamResultByIdAndStudent(Long id, Student student);

    @Query("SELECT e.score FROM ExamResult e WHERE e.examPaper.id = :examPaperId")
    List<Double> findScoresByExamPaperId(@Param("examPaperId") Long examPaperId);

    List<ExamResult> findByExamPaperAndStudent(ExamPaper examPaper, Student student);
    Optional<ExamResult> findByExamPaperAndStudentAndAttempt(ExamPaper examPaper, Student student, Integer attempt);


}
