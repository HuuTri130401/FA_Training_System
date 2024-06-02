package com.fptacademy.training.web.vm;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
public record UserVM(

        @NotBlank(message = "Full name must not be empty")
        @Pattern(regexp = "^[a-zA-Z ]{3,50}$", message = "Invalid name")
        @Schema(example = "Tran Huu Tri")
        String fullName,

        @NotBlank(message = "Email must be not empty")
        @Size(max = 50, message = "Invalid, Email too long")
        @Email(message = "Invalid email")
        String email,

        @NotBlank
        @Schema(example = "2001-04-13")
        String birthday,

        @NotBlank(message = "Gender must be not empty")
        @Pattern(regexp = "^(male|female)$", message = "Gender must be 'male' or 'female'")
        @Schema(example = "male")
        String gender,

        @NotBlank(message = "Activated must be not empty")
        @Pattern(regexp = "^(true|false)$", message = "Activated must be 'true' or 'false'")
        @Schema(example = "true")
        String activated,

        @NotBlank(message = "Level must be not empty")
        String level,

        @Schema(defaultValue = "TRAINEE")
        @NotBlank(message = "Role must be not empty")
        String role,

        @NotBlank(message = "Avatar Url must be not empty")
        String avatarUrl,

        @NotBlank(message = "Code must be not empty")
        String code,

        @Schema(defaultValue = "ACTIVE")
        @NotBlank(message = "Status must be not empty")
        String status

        ) {}
