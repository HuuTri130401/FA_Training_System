package com.fptacademy.training.security.jwt;

import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.repository.UserRepository;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.util.TestUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenProviderTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    private final long ONE_MINUTE = 1;
    private User user;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenProvider, "accessExpireTimeInMinutes", ONE_MINUTE);
        ReflectionTestUtils.setField(tokenProvider, "refreshExpireTimeInMinutes", ONE_MINUTE);
        Role role = RoleFactory.createRoleWithPermissions(Permissions.CLASS_CREATE);
        roleRepository.saveAndFlush(role);
        user = UserFactory.createActiveUser(role);
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    void teardown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testAccessTokenReturnTrueWhenValid() {
        Authentication authentication = TestUtil.createAuthentication(user);
        String token = tokenProvider.generateAccessToken(authentication);

        boolean isTokenValid = tokenProvider.validateAccessToken(token);

        assertThat(isTokenValid).isTrue();
    }

    @Test
    void testAccessTokenReturnFalseWhenJWTisInvalid() {
        boolean isTokenValid = tokenProvider.validateAccessToken("");

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testAccessTokenReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "accessExpireTimeInMinutes", -ONE_MINUTE);

        Authentication authentication = TestUtil.createAuthentication(user);
        String token = tokenProvider.generateAccessToken(authentication);

        boolean isTokenValid = tokenProvider.validateAccessToken(token);

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testRefreshTokenReturnTrueWhenValid() {
        String token = tokenProvider.generateRefreshToken(user.getEmail());

        boolean isTokenValid = tokenProvider.validateRefreshToken(token);

        assertThat(isTokenValid).isTrue();
    }

    @Test
    void testRefreshTokenReturnFalseWhenJWTisInvalid() {
        boolean isTokenValid = tokenProvider.validateAccessToken("");

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testRefreshTokenReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "refreshExpireTimeInMinutes", -ONE_MINUTE);

        String token = tokenProvider.generateRefreshToken(user.getEmail());

        boolean isTokenValid = tokenProvider.validateRefreshToken(token);

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void testGetAuthenticationFromAccessToken() {
        Authentication auth1 = TestUtil.createAuthentication(user);
        String token = tokenProvider.generateAccessToken(auth1);
        Authentication auth2 = tokenProvider.getAuthentication(token);
        assertThat(auth1.getName()).isEqualTo(auth2.getName());
        assertThat(auth1.getAuthorities()).isEqualTo(auth2.getAuthorities());
    }

    @Test
    void testGenerateAccessTokenFromRefreshToken() {
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        boolean isValidToken = tokenProvider.validateAccessToken(accessToken);
        assertThat(isValidToken).isTrue();
    }
}