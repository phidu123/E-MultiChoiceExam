package vn.edu.tdtu.exam.service;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.exam.dto.ExamPaperDTO;
import vn.edu.tdtu.exam.entity.*;
import vn.edu.tdtu.exam.repository.ExamPaperRepository;


import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service("ExamPaperService")
public class ExamPaperService {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 6;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Autowired
    private ExamPaperRepository testRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OptionService optionService;

    public Page<ExamPaper> getTestsBySubject(Long subjectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return testRepository.findBySubjectIdAndIsActiveTrue(subjectId, pageable);
    }

    public Integer getTestQuantityBySubject(Long id) {
        return testRepository.findBySubjectIdAndIsActiveTrue(id).size();
    }

    public Boolean updateTest(Long id, ExamPaperDTO form) {
        ExamPaper existingExamPaper = testRepository.findById(id).orElse(null);
        if (existingExamPaper != null) {
            existingExamPaper.setTitle(form.getTitle());
            existingExamPaper.setLastModified(LocalDateTime.now());
            existingExamPaper.setDuration(form.getDuration());
            existingExamPaper.setTimesAllowed(form.getTimesAllowed());
            existingExamPaper.setShowScore(form.getShowScore());
            existingExamPaper.setExam(examService.getExamById(form.getCategory()));

            testRepository.save(existingExamPaper);
            return true;
        }
        return false;
    }

    public Boolean addTest(Long teacherId, ExamPaperDTO form, MultipartFile file) {
        // read data from the submitted form
        ExamPaper examPaper = convertDTOtoEntity(form);
        examPaper.setTeacher(accountService.getTeacher(teacherId));
        examPaper.setDateCreated(LocalDateTime.now());
        examPaper.setLastModified(LocalDateTime.now());
        examPaper.setAccessToken(generateRandomToken());
        examPaper.setIsActive(true);

        // save file to upload directory
        String fileUrl = saveFile(teacherId, file);
        examPaper.setFile(fileUrl);

        ExamPaper savedPaper = testRepository.save(examPaper);
        Boolean result = readFile(file, savedPaper);
        return result;
    }

    private String saveFile(Long teacherId, MultipartFile file) {
        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = "test_" + teacherId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Copy the file to the specified path
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private ExamPaper convertDTOtoEntity(ExamPaperDTO form) {
        ExamPaper examPaper = new ExamPaper();
        examPaper.setTitle(form.getTitle());
        examPaper.setSubject(subjectService.getSubjectById(form.getSubject()));
        examPaper.setExam(examService.getExamById(form.getCategory()));
        examPaper.setDuration(form.getDuration());
        examPaper.setTimesAllowed(form.getTimesAllowed());
        examPaper.setShowScore(form.getShowScore());
        return examPaper;
    }

    private Boolean readFile(MultipartFile file, ExamPaper examPaper) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            try {
                List<String[]> csvData = reader.readAll();
                for (String[] row : csvData) {
                    Question question = new Question();
                    question.setContent(row[0]);
                    question.setExamPaper(examPaper);
                    Question savedQuestion = questionService.addQuestion(question);

                    int correct = Integer.parseInt(row[5]);
                    for (int i = 1; i <= 4; ++i) {
                        Boolean isCorrect = (correct == i);
                        Option option = new Option();
                        option.setContent(row[i]);
                        option.setIsCorrect(isCorrect);
                        option.setQuestion(savedQuestion);
                        optionService.addOption(option);
                    }
                }

                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            token.append(randomChar);
        }

        return token.toString();
    }

    public Boolean deletePaper(Long id) {
        try {
            ExamPaper examPaper = testRepository.findById(id).get();
            // Set isActive to false
            examPaper.setIsActive(false);

            // Save the updated ExamPaper
            testRepository.save(examPaper);

            return true; // Deactivation success
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean checkAuthor(Long testId, Long userId) {
        return testRepository.findTeacherIdByExamPaperId(testId).equals(userId);
    }

    public ExamPaper getTestsById(Long id) {
        return testRepository.findById(id).orElse(null);
    }
    public ExamPaper getTestByAccessToken(String token) {
        Optional<ExamPaper> examPaper = testRepository.findExamPaperByAccessToken(token);
        if(examPaper.isPresent()){
            return examPaper.get();
        }
        return null;
    }

    public List<ExamPaper> getAllTestByExamId(Long id){
        return testRepository.findAllExamPaperByExamIdAndIsActiveTrue(id);
    }
    public List<ExamPaper> getAllTestBySubject(Subject subject){
        return testRepository.findAllExamPaperBySubjectAndIsActiveTrue(subject);
    }

    public LocalTime getTimeStart(Exam exam) {
        LocalDateTime time = exam.getExamDate();
        return time.toLocalTime();
    }
    public LocalDateTime getTimeEnd(Exam exam, ExamPaper examPaper) {
        LocalDateTime time = exam.getExamDate();
        int duration = examPaper.getDuration();
        return time.plusMinutes(duration);
    }
    public List<Exam> getExamByTeacherAndSubject(Long userId, Long id) {
        return testRepository.findExamIdsByTeacherIdAndSubjectId(userId, id);
    }

    public Long getTestForStatistic(Long subject, Long exam, Long teacher) {
        return testRepository.findExamPaperIdByAttributes(subject, exam, teacher).orElse(null);
    }
}
