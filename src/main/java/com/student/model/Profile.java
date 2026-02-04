package com.student.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "roll_no")
    private String rollNo;
    
    @Column(name = "branch")
    private String branch;
    
    @Column(name = "role")
    private String role = "STUDENT";
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "contact_info")
    private String contactInfo;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
