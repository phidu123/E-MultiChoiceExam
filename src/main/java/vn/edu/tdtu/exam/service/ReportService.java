package vn.edu.tdtu.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.exam.entity.Report;
import vn.edu.tdtu.exam.entity.Student;
import vn.edu.tdtu.exam.repository.ReportRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public Report add(Report report){
        return reportRepository.save(report);
    }
    public List<Report> getAllReports(){
        return reportRepository.findAll();
    }
    public Report getReportById(Long id){
        Optional<Report> result = reportRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }
    public List<Report> getAllStudentReport(Student student){
        return reportRepository.findAllReportByStudent(student);
    }
    public Report updateReport(Long id, Report report){
        Optional<Report> result = reportRepository.findById(id);
        if(result.isPresent()){
            Report r =  result.get();
            r.setId(report.getId());
            r.setStudent(report.getStudent());
            r.setStatus(report.getStatus());
            r.setDescription(report.getDescription());

            return reportRepository.save(r);
        }
        return null;
    }

    public void deleteReportById(Long id){
        reportRepository.deleteById(id);
    }
}
