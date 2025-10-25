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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final PasswordEncoder passwordEncoder; 


    private final EmailService emailService;

    private final ProfileRepository profileRepository;

    private final AuthenticationManager authenticationManager; 


    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        ProfileEntity newProfile= toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        //send activation email
        String activationLink= "http://localhost:8080/api/v1.0/activate?token="+newProfile.getActivationToken();
        String subject="Activate your FinTrack account.";
        String body="Click here to activate your account : "+ activationLink;
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
        return profileRepository.findByActivationToken(token).map(profile -> {
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        }).orElse(false);
    }


    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }


    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
        .orElseThrow(()-> new RuntimeException("Profile not found"));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser=null;
        if(email==null){
            currentUser=getCurrentProfile();
        }else{
            currentUser=profileRepository.findByEmail(email)
            .orElseThrow(()-> new RuntimeException("Profile not found"));   
        }


        return ProfileDTO.builder()
        .id(currentUser.getId())
        .fullname(currentUser.getFullname())
        .email(currentUser.getEmail())
        .profileImageUrl(currentUser.getProfileImageUrl())
        .createdAt(currentUser.getCreatedAt())
        .updatedAt(currentUser.getUpdatedAt())
        .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            return Map.of("token","JWT token",
            "user",getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
