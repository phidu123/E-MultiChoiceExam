package vn.edu.tdtu.exam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "teacher")
public class Teacher extends Account {
    @Column(name = "field")
    private String field;

    @Column(name = "position")
    private String position;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "degree")
    private String degree;

    @Column(name = "educational_bg")
    private String educationalBackground;
}
