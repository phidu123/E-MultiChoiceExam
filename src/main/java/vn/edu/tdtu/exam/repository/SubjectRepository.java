package vn.edu.tdtu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.ExamPaper;
import vn.edu.tdtu.exam.entity.Student;
import vn.edu.tdtu.exam.entity.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("SELECT s FROM Subject s " +
            "WHERE s.id IN " +
                "(SELECT ts.subject.id FROM TeacherSubject ts " +
                "WHERE ts.teacher.id = :teacherId AND ts.isActive = true)")
    List<Subject> findSubjectsByTeacherId(@Param("teacherId") Long teacherId);

    @Query(value = "SELECT COUNT(DISTINCT e.subject.id) FROM ExamPaper e WHERE e.subject.id = :subjectId")
    Integer findExamPaperQuantity(Long subjectId);

    @Query(value = "SELECT COUNT(DISTINCT e.student.id) FROM StudentSubject e WHERE e.subject.id = :subjectId")
    Integer countStudent(Long subjectId);

    Subject findByName(String name);

}
