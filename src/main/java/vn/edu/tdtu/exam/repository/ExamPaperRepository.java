package vn.edu.tdtu.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Exam;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.Subject;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
    Page<ExamPaper> findBySubjectIdAndIsActiveTrue(Long subjectId, Pageable pageable);

    List<ExamPaper> findBySubjectIdAndIsActiveTrue(Long subjectId);

    @Query("SELECT e.teacher.id FROM ExamPaper e WHERE e.id = :examPaperId")
    Long findTeacherIdByExamPaperId(@Param("examPaperId") Long examPaperId);

    List<ExamPaper> findAllExamPaperByExamIdAndIsActiveTrue(Long id);
    List<ExamPaper> findAllExamPaperByExamId(Long id);
    List<ExamPaper> findAllExamPaperBySubjectAndIsActiveTrue(Subject subject);
    Optional<ExamPaper> findExamPaperByAccessToken(String accessToken);

    @Query("SELECT e.exam FROM ExamPaper e WHERE e.teacher.id = :teacherId AND e.subject.id = :subjectId")
    List<Exam> findExamIdsByTeacherIdAndSubjectId(
            @Param("teacherId") Long teacherId,
            @Param("subjectId") Long subjectId
    );

    @Query("SELECT e.id FROM ExamPaper e WHERE e.subject.id = :subject AND e.exam.id = :exam AND e.teacher.id = :teacher ORDER BY e.id ASC")
    Optional<Long> findExamPaperIdByAttributes(@Param("subject") Long subject, @Param("exam") Long exam, @Param("teacher") Long teacher);

}
