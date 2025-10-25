package com.anudeep.budgetmanager.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {


    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
        .orElseThrow(()-> new UsernameNotFoundException("Profile not dound with email"));
        return User.builder()
        .username(existingProfile.getEmail())
        .password(existingProfile.getPassword())
        .authorities(Collections.emptyList())
        .build(); //roles/authorities
    }
}
