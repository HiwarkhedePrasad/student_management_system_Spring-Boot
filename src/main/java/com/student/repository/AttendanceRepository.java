package com.student.repository;

import com.student.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByStudentId(UUID studentId);
    
    List<Attendance> findByCourseIdAndDate(Long courseId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a JOIN FETCH a.course WHERE a.studentId = :studentId ORDER BY a.date DESC")
    List<Attendance> findByStudentIdWithCourse(UUID studentId);
    
    @Query("SELECT a FROM Attendance a JOIN FETCH a.student WHERE a.courseId = :courseId AND a.date = :date")
    List<Attendance> findByCourseIdAndDateWithStudent(Long courseId, LocalDate date);
    
    boolean existsByStudentIdAndCourseIdAndDate(UUID studentId, Long courseId, LocalDate date);
}
