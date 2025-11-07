package com.anudeep.budgetmanager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.anudeep.budgetmanager.dto.AuthDTO;
import com.anudeep.budgetmanager.dto.ProfileDTO;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.ExpenseRepository;
import com.anudeep.budgetmanager.repository.IncomeRepository;
import com.anudeep.budgetmanager.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional  // ✅ ADDED: Auto-rollback after each test
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String uniqueEmail;

    @BeforeEach
    public void setUp() {
        // ✅ ADDED: Generate unique email for each test
        uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    // ✅ ADDED: Helper method to cleanup related data
    private void cleanupProfileData(ProfileEntity profile) {
        if (profile != null) {
            // Delete all incomes for this profile first
            incomeRepository.deleteAll(incomeRepository.findByProfileIdOrderByDateDesc(profile.getId()));
            
            // Delete all expenses for this profile
            expenseRepository.deleteAll(expenseRepository.findByProfileIdOrderByDateDesc(profile.getId()));
            
            // Now delete the profile
            profileRepository.delete(profile);
        }
    }

    @Test
    public void testRegisterProfile_Success() throws Exception {
        ProfileDTO profileDTO = ProfileDTO.builder()
            .fullname("John Doe")
            .email(uniqueEmail)
            .password("Password@123")
            .profileImageUrl("https://example.com/image.jpg")
            .build();

        mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(profileDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Create and activate user
        ProfileEntity user = createAndActivateUser(uniqueEmail, "Password@123");

        AuthDTO authDTO = AuthDTO.builder()
            .email(uniqueEmail)
            .password("Password@123")
            .build();

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());

        cleanupProfileData(user);
    }

    @Test
    public void testLogin_InvalidPassword() throws Exception {
        ProfileEntity user = createAndActivateUser(uniqueEmail, "Password@123");

        AuthDTO authDTO = AuthDTO.builder()
            .email(uniqueEmail)
            .password("WrongPassword")
            .build();

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDTO)))
            .andExpect(status().isUnauthorized());

        cleanupProfileData(user);
    }

    @Test
    public void testLogin_InactiveAccount() throws Exception {
        // ✅ ADDED: Use unique email
        String inactiveEmail = "inactive_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        
        // Create user but don't activate
        ProfileEntity profile = ProfileEntity.builder()
            .fullname("Inactive User")
            .email(inactiveEmail)
            .password(passwordEncoder.encode("Password@123"))
            .isActive(false)
            .activationToken(UUID.randomUUID().toString())
            .build();
        profileRepository.save(profile);

        AuthDTO authDTO = AuthDTO.builder()
            .email(inactiveEmail)
            .password("Password@123")
            .build();

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDTO)))
            .andExpect(status().isForbidden());

        cleanupProfileData(profile);
    }

    @Test
    public void testActivateProfile_Success() throws Exception {
        String activationToken = UUID.randomUUID().toString();
        ProfileEntity profile = ProfileEntity.builder()
            .fullname("Test User")
            .email(uniqueEmail)
            .password(passwordEncoder.encode("Password@123"))
            .isActive(false)
            .activationToken(activationToken)
            .build();
        profileRepository.save(profile);

        mockMvc.perform(get("/activate?token=" + activationToken))
            .andExpect(status().isOk())
            .andExpect(content().string("Profile activated successfully."));

        cleanupProfileData(profile);
    }

    @Test
    public void testActivateProfile_InvalidToken() throws Exception {
        mockMvc.perform(get("/activate?token=invalid_token"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testAccessSecuredEndpoint_WithValidToken() throws Exception {
        // ✅ ADDED: Use unique email
        String secureEmail = "secure_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        ProfileEntity user = createAndActivateUser(secureEmail, "Password@123");

        AuthDTO authDTO = AuthDTO.builder()
            .email(secureEmail)
            .password("Password@123")
            .build();

        // Login to get token
        String loginResponse = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDTO)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Access dashboard with token
        mockMvc.perform(get("/dashboard")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        cleanupProfileData(user);
    }

    @Test
    public void testAccessSecuredEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isUnauthorized());
    }

    // ✅ FIXED: Helper method to create and activate user with unique email
    private ProfileEntity createAndActivateUser(String email, String password) {
        ProfileEntity profile = ProfileEntity.builder()
            .fullname("Test User")
            .email(email)
            .password(passwordEncoder.encode(password))
            .isActive(true)
            .activationToken(UUID.randomUUID().toString())
            .build();
        return profileRepository.save(profile);
    }
}