package com.fptacademy.training.factory;

import com.fptacademy.training.domain.Role;
import com.github.javafaker.Faker;

import java.util.Arrays;

public class RoleFactory {
    public static Role createRoleWithPermissions(String... permissions) {
        Faker faker = new Faker();
        return Role.builder()
                .name(faker.funnyName().name())
                .permissions(Arrays.asList(permissions))
                .build();
    }
}
