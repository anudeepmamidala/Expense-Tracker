package com.anudeep.budgetmanager.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.anudeep.budgetmanager.dto.AuthDTO;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper; // Helper to convert objects to JSON

    // Clean up the database after each test
    @AfterEach
    public void cleanup() {
        profileRepository.deleteAll();
    }

    // --- Helper Method to create a user ---
    private ProfileEntity createAndActivateUser(String email, String password) {
        ProfileEntity user = ProfileEntity.builder()
                .email(email)
                .fullname("Test User")
                .password(passwordEncoder.encode(password))
                .isActive(true) // Activate them directly for this test
                .build();
        return profileRepository.save(user);
    }

    @Test
    public void testLogin_Success() throws Exception {
        // 1. Setup: Create an active user in the database
        createAndActivateUser("test@example.com", "password123");

        // 2. Prepare Login DTO
        // AuthDTO authDTO = new AuthDTO("test@example.com", "password123"); // <-- ❗️ OLD (WRONG)
        AuthDTO authDTO = new AuthDTO(); // <-- ❗️ FIX
        authDTO.setEmail("test@example.com"); // <-- ❗️ FIX
        authDTO.setPassword("password123"); // <-- ❗️ FIX
        
        String authDtoJson = objectMapper.writeValueAsString(authDTO);

        // 3. Perform: Call POST /login
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authDtoJson))
                // 4. Assert: Check the results
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue())) // Check that a token was returned
                .andExpect(jsonPath("$.user.email", is("test@example.com"))); // Check user details
    }

    @Test
    public void testLogin_InvalidPassword() throws Exception {
        // 1. Setup
        createAndActivateUser("test@example.com", "password123");
        
        // AuthDTO authDTO = new AuthDTO("test@example.com", "wrong-password"); // <-- ❗️ OLD (WRONG)
        AuthDTO authDTO = new AuthDTO(); // <-- ❗️ FIX
        authDTO.setEmail("test@example.com"); // <-- ❗️ FIX
        authDTO.setPassword("wrong-password"); // <-- ❗️ FIX
        
        String authDtoJson = objectMapper.writeValueAsString(authDTO);

        // 2. Perform & Assert
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authDtoJson))
                .andExpect(status().isUnauthorized()) // Expect 401
                .andExpect(jsonPath("$.message", is("Invalid email or password.")));
    }

    @Test
    public void testLogin_InactiveAccount() throws Exception {
        // 1. Setup: Create an INACTIVE user
        ProfileEntity user = ProfileEntity.builder()
                .email("inactive@example.com")
                .fullname("Inactive User")
                .password(passwordEncoder.encode("password123"))
                .isActive(false) // <-- Account is not active
                .activationToken("some-token")
                .build();
        profileRepository.save(user);

        // AuthDTO authDTO = new AuthDTO("inactive@example.com", "password123"); // <-- ❗️ OLD (WRONG)
        AuthDTO authDTO = new AuthDTO(); // <-- ❗️ FIX
        authDTO.setEmail("inactive@example.com"); // <-- ❗️ FIX
        authDTO.setPassword("password123"); // <-- ❗️ FIX
        
        String authDtoJson = objectMapper.writeValueAsString(authDTO);

        // 2. Perform & Assert
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authDtoJson))
                .andExpect(status().isForbidden()) // Expect 403
                .andExpect(jsonPath("$.message", is("Account is not activated. Please check your email for activation link.")));
    }

    @Test
    public void testAccessSecuredEndpoint_WithValidToken() throws Exception {
        // 1. Setup: Login to get a valid token
        createAndActivateUser("secure@example.com", "password123");
        
        // AuthDTO authDTO = new AuthDTO("secure@example.com", "password123"); // <-- ❗️ OLD (WRONG)
        AuthDTO authDTO = new AuthDTO(); // <-- ❗️ FIX
        authDTO.setEmail("secure@example.com"); // <-- ❗️ FIX
        authDTO.setPassword("password123"); // <-- ❗️ FIX
        
        MvcResult loginResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // 2. Extract the token from the response
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // 3. Perform: Call a secured endpoint (e.g., "/") with the token
        // Note: Your config secures everything except /register, /, and /activate
        // Let's try to access a hypothetical secured endpoint like "/api/profile"
        // (This endpoint doesn't exist, so we expect a 404, *not* a 401 or 403)
        // If you had a real secured endpoint, you'd test it here.
        
        // For now, let's just test a random secured path
        mockMvc.perform(get("/some-secured-path")
                .header("Authorization", "Bearer " + token))
                // We expect 404 Not Found because the path doesn't exist.
                // But if the token was *invalid*, we'd get a 401 Unauthorized.
                // This proves the token *was* valid.
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAccessSecuredEndpoint_NoToken() throws Exception {
        // Perform: Call a secured endpoint without any token
        mockMvc.perform(get("/some-secured-path"))
                .andExpect(status().isForbidden()); // <-- ❗️ This is the correct fix
    }
}