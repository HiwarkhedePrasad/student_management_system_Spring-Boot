package com.student.controller;

import com.student.model.*;
import com.student.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AdminController(ProfileRepository profileRepository,
                          CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository,
                          AttendanceRepository attendanceRepository,
                          PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", profileRepository.findAllStudents().size());
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("enrollmentCount", enrollmentRepository.count());
        return "admin/dashboard";
    }
    
    // ============ STUDENT MANAGEMENT ============
    
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", profileRepository.findAllStudents());
        return "admin/students";
    }
    
    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Profile());
        return "admin/student-form";
    }
    
    @PostMapping("/students")
    public String saveStudent(@ModelAttribute Profile student) {
        if (student.getId() == null) {
            student.setId(UUID.randomUUID());
            student.setPassword(passwordEncoder.encode("password123")); // Default password
            student.setRole("STUDENT");
            student.setIsDeleted(false);
        }
        profileRepository.save(student);
        return "redirect:/admin/students";
    }
    
    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable UUID id, Model model) {
        model.addAttribute("student", profileRepository.findById(id).orElseThrow());
        return "admin/student-form";
    }
    
    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable UUID id) {
        Profile student = profileRepository.findById(id).orElseThrow();
        student.setIsDeleted(true);
        profileRepository.save(student);
        return "redirect:/admin/students";
    }
    
    // ============ COURSE MANAGEMENT ============
    
    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/courses";
    }
    
    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "admin/course-form";
    }
    
    @PostMapping("/courses")
    public String saveCourse(@ModelAttribute Course course) {
        courseRepository.save(course);
        return "redirect:/admin/courses";
    }
    
    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id).orElseThrow());
        return "admin/course-form";
    }
    
    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/courses";
    }
    
    // ============ GRADE ENTRY ============
    
    @GetMapping("/grades")
    public String gradesPage(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/grades";
    }
    
    @GetMapping("/grades/{courseId}")
    public String gradesByCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseRepository.findById(courseId).orElseThrow());
        model.addAttribute("enrollments", enrollmentRepository.findByCourseIdWithStudent(courseId));
        return "admin/grade-entry";
    }
    
    @PostMapping("/grades/save")
    public String saveGrades(@RequestParam Long courseId,
                            @RequestParam List<Long> enrollmentIds,
                            @RequestParam List<Double> marks) {
        for (int i = 0; i < enrollmentIds.size(); i++) {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentIds.get(i)).orElseThrow();
            enrollment.setMarks(marks.get(i));
            enrollment.setGpa(calculateGpa(marks.get(i)));
            enrollment.setGrade(calculateGrade(marks.get(i)));
            enrollmentRepository.save(enrollment);
        }
        return "redirect:/admin/grades";
    }
    
    // ============ ATTENDANCE ============
    
    @GetMapping("/attendance")
    public String attendancePage(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("today", LocalDate.now());
        return "admin/attendance";
    }
    
    @GetMapping("/attendance/{courseId}")
    public String attendanceByCourse(@PathVariable Long courseId,
                                     @RequestParam(required = false) LocalDate date,
                                     Model model) {
        if (date == null) date = LocalDate.now();
        model.addAttribute("course", courseRepository.findById(courseId).orElseThrow());
        model.addAttribute("enrollments", enrollmentRepository.findByCourseIdWithStudent(courseId));
        model.addAttribute("date", date);
        model.addAttribute("existingAttendance", attendanceRepository.findByCourseIdAndDate(courseId, date));
        return "admin/attendance-entry";
    }
    
    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam Long courseId,
                                @RequestParam LocalDate date,
                                @RequestParam List<UUID> studentIds,
                                @RequestParam List<String> statuses) {
        for (int i = 0; i < studentIds.size(); i++) {
            if (!attendanceRepository.existsByStudentIdAndCourseIdAndDate(studentIds.get(i), courseId, date)) {
                Attendance attendance = new Attendance();
                attendance.setStudentId(studentIds.get(i));
                attendance.setCourseId(courseId);
                attendance.setDate(date);
                attendance.setStatus(statuses.get(i));
                attendanceRepository.save(attendance);
            }
        }
        return "redirect:/admin/attendance";
    }
    
    // ============ HELPERS ============
    
    private String calculateGrade(Double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
    
    private Double calculateGpa(Double marks) {
        if (marks >= 90) return 4.0;
        if (marks >= 80) return 3.5;
        if (marks >= 70) return 3.0;
        if (marks >= 60) return 2.5;
        if (marks >= 50) return 2.0;
        return 0.0;
    }
}
