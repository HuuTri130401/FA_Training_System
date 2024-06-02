package com.fptacademy.training.web.vm;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record NoNullRequiredUserVM(

        @Pattern(regexp = "^[a-zA-Z ]{3,50}$", message = "Invalid name")
        @Schema(example = "Tran Huu Tri")
        String fullName,

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid birth day, 'yyyy-mm-dd' format is required!")
        @Schema(example = "2002-10-19")
        String birthday,

        @Pattern(regexp = "^(male|female)$", message = "Gender must be 'male' or 'female'")
        @Schema(example = "male")
        String gender,

        String currentPassword,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$",
                message = "Password must contains at least one uppercase \n" +
                          "Password must contains at least one specific character \n" +
                          "Password can't contains white space")
        @Size(min = 6, message = "Password must have at least 6 character")
        @Size(max = 50, message = "Invalid, password too long")
        @Schema(example = "Tricho@123")
        String newPassword,

        String avatarUrl

) {
}
