package com.fptacademy.training.service.mapper;


import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.fptacademy.training.domain.User;
import com.fptacademy.training.domain.enumeration.UserStatus;
import com.fptacademy.training.service.LevelService;
import com.fptacademy.training.service.RoleService;
import com.fptacademy.training.service.dto.ReturnPageDto;
import com.fptacademy.training.service.dto.UserDto;
import com.fptacademy.training.web.vm.UserVM;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserDto toDto(User user){
        if(user == null){
            return null;
        }
        UserDto dto = modelMapper.map(user, UserDto.class);
        return dto;
    }

    public User toEntity(UserVM userVM, LevelService levelService, RoleService roleService){
        if(userVM == null && levelService == null && roleService == null){
            return null;
        }
        User user = new User();
        if(userVM != null){
            user.setFullName(userVM.fullName());
            user.setEmail(userVM.email());
            user.setBirthday(LocalDate.parse(userVM.birthday()));
            user.setGender(Boolean.valueOf(userVM.gender()));
            user.setActivated(Boolean.valueOf(userVM.activated()));
            user.setAvatarUrl(userVM.avatarUrl());
            user.setCode(userVM.code());
            user.setStatus(UserStatus.valueOf(userVM.status()));
        }
        user.setLevel(levelService.getLevelByName(userVM.level()));
        user.setRole(roleService.getRoleByName(userVM.role()));
        return user;
    }

    public List<UserDto> toDtos(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }

    public ReturnPageDto<List<UserDto>> toPageUserDto(Page<User> users){
        ReturnPageDto<List<UserDto>> pageUserDto = new ReturnPageDto<>();

        pageUserDto.setTotalPages(users.getTotalPages());
        pageUserDto.setTotalElements(users.getTotalElements());
        pageUserDto.setSize(users.getSize());
        pageUserDto.setPage(users.getNumber());

        pageUserDto.setContents(toDtos(users.toList()));

        return pageUserDto;
    }
}
