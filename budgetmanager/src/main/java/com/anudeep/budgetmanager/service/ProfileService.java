package com.anudeep.budgetmanager.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.AuthDTO;
import com.anudeep.budgetmanager.dto.ProfileDTO;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.ProfileRepository;
import com.anudeep.budgetmanager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final AuthenticationManager authenticationManager;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        String activationLink = "http://localhost:8080/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your FinTrack Account";
        String body = "Click here to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
            .id(profileDTO.getId())
            .fullname(profileDTO.getFullname())
            .email(profileDTO.getEmail())
            .password(passwordEncoder.encode(profileDTO.getPassword()))
            .profileImageUrl(profileDTO.getProfileImageUrl())
            .createdAt(profileDTO.getCreatedAt())
            .updatedAt(profileDTO.getUpdatedAt())
            .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
            .id(profileEntity.getId())
            .fullname(profileEntity.getFullname())
            .email(profileEntity.getEmail())
            .profileImageUrl(profileEntity.getProfileImageUrl())
            .createdAt(profileEntity.getCreatedAt())
            .updatedAt(profileEntity.getUpdatedAt())
            .build();
    }

    public boolean activateProfile(String token) {
        ProfileEntity profile = profileRepository.findByActivationToken(token).orElse(null);
        if (profile != null) {
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        }
        return false;
    }

    public boolean isAccountActive(String email) {
        ProfileEntity profileEntity = profileRepository.findByEmail(email).orElse(null);
        if (profileEntity != null) {
            return profileEntity.getIsActive(); // ✅ change to match field name
        } else {
            return false;
        }
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email).orElse(null);
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email).orElse(null);
        }
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        } else {
            return toDTO(currentUser);
        }
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Credentials");
        }
    }
}
