package com.fptacademy.training.security;

import static org.assertj.core.api.Assertions.*;

import com.fptacademy.training.IntegrationTest;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@IntegrationTest
class UserDetailsServiceImplIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    private User user;

    @BeforeEach
    void setup() {
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
    void testFindUserByEmail() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    void testFindUserByEmailFails() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userDetailsService.loadUserByUsername("wrong-email"));
    }
}