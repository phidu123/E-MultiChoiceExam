package vn.edu.tdtu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Subject;
import vn.edu.tdtu.exam.entity.TeacherSubject;

import java.util.List;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {

    @Query("SELECT ts.schedule FROM TeacherSubject ts WHERE ts.subject.id = :subjectId")
    String findScheduleIdBySubjectId(@Param("subjectId") Long subjectId);
}
