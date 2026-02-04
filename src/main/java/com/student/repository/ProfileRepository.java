package com.student.repository;

import com.student.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    
    Optional<Profile> findByEmail(String email);
    
    @Query("SELECT p FROM Profile p WHERE p.role = 'STUDENT' AND (p.isDeleted = false OR p.isDeleted IS NULL)")
    List<Profile> findAllStudents();
    
    @Query("SELECT p FROM Profile p WHERE p.isDeleted = false OR p.isDeleted IS NULL")
    List<Profile> findAllActive();
    
    List<Profile> findByBranch(String branch);
    
    Optional<Profile> findByRollNo(String rollNo);
}
