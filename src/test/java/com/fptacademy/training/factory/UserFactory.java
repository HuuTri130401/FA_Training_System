package com.fptacademy.training.factory;

import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFactory {
    public static User createActiveUser(Role role) {
        Faker faker = new Faker();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.random().hex(30)))
                .code(faker.random().hex(10))
                .activated(true)
                .role(role)
                .avatarUrl(faker.avatar().image())
                .build();
    }

    public static User createActiveUser(String password, Role role) {
        Faker faker = new Faker();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(password))
                .code(faker.random().hex(10))
                .activated(true)
                .role(role)
                .avatarUrl(faker.avatar().image())
                .build();
    }
}
