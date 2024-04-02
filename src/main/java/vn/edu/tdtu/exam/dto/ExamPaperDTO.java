package vn.edu.tdtu.exam.dto;

import lombok.Data;

@Data
public class ExamPaperDTO {
    private String title;
    private Long subject;
    private Long category;
    private Integer duration;
    private Integer timesAllowed;
    private Boolean showScore;
}
