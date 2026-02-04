package com.student.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_code", unique = true)
    private String courseCode;
    
    @Column(name = "course_name")
    private String courseName;
    
    @Column(name = "credits")
    private Integer credits;
    
    @Column(name = "teacher_name")
    private String teacherName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
