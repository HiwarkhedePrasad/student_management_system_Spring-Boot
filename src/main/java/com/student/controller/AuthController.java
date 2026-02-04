package com.student.controller;

import com.student.model.Profile;
import com.student.repository.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
public class AuthController {
    
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("profile", new Profile());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute Profile profile, Model model) {
        if (profileRepository.findByEmail(profile.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        
        profile.setId(UUID.randomUUID());
        profile.setPassword(passwordEncoder.encode(profile.getPassword()));
        profile.setRole("STUDENT");
        profile.setIsDeleted(false);
        profileRepository.save(profile);
        
        return "redirect:/login?registered=true";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
