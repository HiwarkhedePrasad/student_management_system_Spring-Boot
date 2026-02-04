package com.student.repository;

import com.student.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(UUID studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.studentId = :studentId")
    List<Enrollment> findByStudentIdWithCourse(UUID studentId);
    
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student WHERE e.courseId = :courseId")
    List<Enrollment> findByCourseIdWithStudent(Long courseId);
    
    boolean existsByStudentIdAndCourseId(UUID studentId, Long courseId);
}
