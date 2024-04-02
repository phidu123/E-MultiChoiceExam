package vn.edu.tdtu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.exam.entity.Student;
import vn.edu.tdtu.exam.entity.StudentSubject;
import vn.edu.tdtu.exam.entity.Subject;

import java.util.List;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    List<StudentSubject> findByBanned(Boolean banned);

    @Query("SELECT s FROM StudentSubject s WHERE s.subject.name = :name AND s.banned = false")
    List<StudentSubject> searchBySubjectName(@Param("name") String name);

    List<StudentSubject> findAllStudentSubjectByStudent (Student student);

    StudentSubject findByStudentAndSubject(Student student, Subject subject);

    @Query("SELECT ss FROM StudentSubject ss where ss.student.id = :ssid and ss.subject.id = :subid")
    StudentSubject selectStudent(@Param("ssid") Long ssid, @Param("subid") Long subid);

    @Query("SELECT s.banned FROM StudentSubject s where s.student.id = :id and s.subject.id = :ssid")
    Boolean findBannedStudent(@Param("id") Long sid, Long ssid);
}
