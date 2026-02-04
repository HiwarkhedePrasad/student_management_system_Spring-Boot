package com.student.controller;

import com.student.model.*;
import com.student.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    
    public StudentController(ProfileRepository profileRepository,
                            CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository,
                            AttendanceRepository attendanceRepository) {
        this.profileRepository = profileRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Profile student = getCurrentStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrollmentRepository.findByStudentIdWithCourse(student.getId()).size());
        return "student/dashboard";
    }
    
    // ============ PROFILE ============
    
    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("student", getCurrentStudent(auth));
        return "student/profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(Authentication auth, @ModelAttribute Profile profileData) {
        Profile student = getCurrentStudent(auth);
        student.setContactInfo(profileData.getContactInfo());
        student.setProfilePicture(profileData.getProfilePicture());
        profileRepository.save(student);
        return "redirect:/student/profile";
    }
    
    // ============ COURSES ============
    
    @GetMapping("/courses")
    public String courses(Authentication auth, Model model) {
        Profile student = getCurrentStudent(auth);
        model.addAttribute("allCourses", courseRepository.findAll());
        model.addAttribute("enrolledCourses", enrollmentRepository.findByStudentId(student.getId()));
        return "student/courses";
    }
    
    @PostMapping("/courses/{courseId}/enroll")
    public String enrollCourse(Authentication auth, @PathVariable Long courseId) {
        Profile student = getCurrentStudent(auth);
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(student.getId());
            enrollment.setCourseId(courseId);
            enrollmentRepository.save(enrollment);
        }
        return "redirect:/student/courses";
    }
    
    // ============ GRADES ============
    
    @GetMapping("/grades")
    public String grades(Authentication auth, Model model) {
        Profile student = getCurrentStudent(auth);
        model.addAttribute("enrollments", enrollmentRepository.findByStudentIdWithCourse(student.getId()));
        return "student/grades";
    }
    
    // ============ ATTENDANCE ============
    
    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {
        Profile student = getCurrentStudent(auth);
        model.addAttribute("attendanceRecords", attendanceRepository.findByStudentIdWithCourse(student.getId()));
        return "student/attendance";
    }
    
    // ============ HELPERS ============
    
    private Profile getCurrentStudent(Authentication auth) {
        return profileRepository.findByEmail(auth.getName()).orElseThrow();
    }
}
