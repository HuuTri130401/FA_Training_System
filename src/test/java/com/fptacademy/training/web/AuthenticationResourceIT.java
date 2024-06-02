package com.fptacademy.training.web;

import com.fptacademy.training.IntegrationTest;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.repository.UserRepository;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.security.jwt.JwtTokenProvider;
import com.fptacademy.training.service.util.TestUtil;
import com.fptacademy.training.web.vm.LoginVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@IntegrationTest
public class AuthenticationResourceIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private MockMvc mockMvc;
    private final String DEFAULT_PASSWORD = "test-password";
    private User user;

    @BeforeEach
    void setup() {
        Role role = RoleFactory.createRoleWithPermissions(Permissions.CLASS_CREATE);
        roleRepository.saveAndFlush(role);
        user = UserFactory.createActiveUser(DEFAULT_PASSWORD, role);
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    void teardown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testLogin() throws Exception {
        LoginVM loginVM = new LoginVM(user.getEmail(), DEFAULT_PASSWORD);

        mockMvc
                .perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(loginVM)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(user.getFullName()))
                .andExpect(jsonPath("$.avatarUrl").value(user.getAvatarUrl()))
                .andExpect(jsonPath("$.tokens.accessToken").isString())
                .andExpect(jsonPath("$.tokens.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokens.refreshToken").isString())
                .andExpect(jsonPath("$.tokens.refreshToken").isNotEmpty());
    }

    @Test
    void testLoginFails() throws Exception {
        LoginVM loginVM = new LoginVM("wrong-email", "wrong-password");
        mockMvc
                .perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(loginVM)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAccessTokenFromRefreshToken() throws Exception {
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        mockMvc
                .perform(get("/api/auth/refresh").header("Refresh-Token", refreshToken))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void testGetAccessTokenFromRefreshTokenFails() throws Exception {
        String refreshToken = "invalid-token";
        mockMvc
                .perform(get("/api/auth/refresh").header("Refresh-Token", refreshToken))
                .andExpect(status().isUnauthorized());
    }
}