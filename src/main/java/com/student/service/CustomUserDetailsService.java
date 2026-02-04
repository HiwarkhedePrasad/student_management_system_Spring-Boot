package com.student.service;

import com.student.model.Profile;
import com.student.repository.ProfileRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final ProfileRepository profileRepository;
    
    public CustomUserDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile profile = profileRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        if (profile.getIsDeleted() != null && profile.getIsDeleted()) {
            throw new UsernameNotFoundException("Account has been deactivated");
        }
        
        return new User(
            profile.getEmail(),
            profile.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + profile.getRole()))
        );
    }
}
