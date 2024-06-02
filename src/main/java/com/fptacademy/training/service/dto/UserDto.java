package com.fptacademy.training.service.dto;

import com.fptacademy.training.domain.Level;
import com.fptacademy.training.domain.Role;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String fullName;

    private String email;

    private String birthday;

    private Boolean gender;

    private Boolean activated;

    private Level level;

    private Role role;

    private String avatarUrl;

    private String code;

    private String status;
}
