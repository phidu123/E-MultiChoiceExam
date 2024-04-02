package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.dto.StudentDTO;
import vn.edu.tdtu.exam.entity.Account;
import vn.edu.tdtu.exam.entity.Student;
import vn.edu.tdtu.exam.repository.AccountRepository;
import vn.edu.tdtu.exam.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @Autowired
    public StudentService(StudentRepository studentRepository, AccountRepository accountRepository) {
        this.studentRepository = studentRepository;
        this.accountRepository = accountRepository;
    }
    public Student add(StudentDTO studentDTO) {
        return studentRepository.save(convertDTOtoEntity(studentDTO));
    }

    public List<Student> getAllStudent(){
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id){
        Optional<Student> result = studentRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }
    public StudentDTO getStudentDTOById(Long id){
        Optional<Student> result = studentRepository.findById(id);
        if(result.isPresent()){
            return convertEntityToDTO(result.get());
        }
        return null;
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO){
        Optional<Student> result = studentRepository.findById(id);
        if(result.isPresent()){
            Student student = result.get();
            student.setName(studentDTO.getStudentName());
            student.setEmail(studentDTO.getEmail());
            student.setMajor(studentDTO.getMajor());
            student.setEnrollment_year(studentDTO.getEnrollment_year());
            student.setAddress(studentDTO.getAddress());
            student.setPhone(studentDTO.getPhone());
            student.setWorkplace(studentDTO.getWorkplace());
            student.setDoB(studentDTO.getDoB());

            Student savedStudent = studentRepository.save(student);
            return convertEntityToDTO(savedStudent);
        }
        return null;
    }

    public void deleteStudentById(Long id){
        studentRepository.deleteById(id);
    }

    private Student convertDTOtoEntity(StudentDTO studentDTO){
        Student student = new Student();
        student.setStudentId(studentDTO.getStudentId());
        student.setName(studentDTO.getStudentName());
        student.setEmail(studentDTO.getEmail());
        student.setMajor(studentDTO.getMajor());
        student.setEnrollment_year(studentDTO.getEnrollment_year());
        student.setAddress(studentDTO.getAddress());
        student.setPhone(studentDTO.getPhone());
        student.setWorkplace(studentDTO.getWorkplace());
        student.setDoB(studentDTO.getDoB());
        student.setAvatar(studentDTO.getAvatar());
        return student;
    }

    private StudentDTO convertEntityToDTO(Student student){
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(student.getStudentId());
        studentDTO.setStudentName(student.getName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setMajor(student.getMajor());
        studentDTO.setEnrollment_year(student.getEnrollment_year());
        studentDTO.setAddress(student.getAddress());
        studentDTO.setPhone(student.getPhone());
        studentDTO.setWorkplace(student.getWorkplace());
        studentDTO.setDoB(student.getDoB());
        studentDTO.setAvatar(student.getAvatar());

        return studentDTO;
    }

    public  List<Student> getListStudent(Long id) {
        return studentRepository.findListStudent(id);
    }
}
