package com.fptacademy.training.web;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fptacademy.training.domain.User;
import com.fptacademy.training.web.vm.NoNullRequiredUserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fptacademy.training.service.UserService;
import com.fptacademy.training.service.dto.UserDto;
import com.fptacademy.training.service.dto.ReturnPageDto;
import com.fptacademy.training.web.api.UserResource;
import com.fptacademy.training.web.vm.UserVM;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class UserResourceImpl implements UserResource {

    private final UserService userService;

    private final ResourceLoader resourceLoader;


    @Override
    public ResponseEntity<UserDto> createUser(UserVM userVM) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userVM));
    }

    @Override
    public ResponseEntity<UserDto> deActiveUser() {
        return null;
    }

    @Override
    public ResponseEntity<UserDto> deleteUser(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deleteUser(id));
    }

    @Override
    public ResponseEntity<UserDto> deActive(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deActive(id));
    }

    @Override
    public ResponseEntity<ReturnPageDto<List<UserDto>>> getUsers(String email, String fullName, String code,
            String levelName, String roleName, String birthdayFrom, String birthdayTo,
            String status, String sort, Integer pageNumber, Integer pageSize) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUsersByFilters(email, fullName, code, levelName,
                        roleName, true, birthdayFrom, birthdayTo, status, sort, pageNumber, pageSize));
    }

    @Override
    public ResponseEntity<Optional<UserDto>> getUserByEmail(String email) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserByEmail(email));
    }

    @Override
    public ResponseEntity<List<UserDto>> importUsersFromExcel(MultipartFile file, String description) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.importUsersToDB(file, description));
    }

    @Override
    public ResponseEntity<List<UserDto>> getUserByName(String name) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findUserByName(name));
    }

    @Override
    public ResponseEntity<?> changeRole(long id, String typeRole) {
        this.userService.changeRole(id, typeRole);
        return ResponseEntity
                .ok(Map.of("Message", "User's role change successfully"));
    }

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<UserDto> updateUser(NoNullRequiredUserVM noNullRequiredUserVM) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(noNullRequiredUserVM));
    }

    @Override
    public ResponseEntity<?> exportUsersToExcel(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = HttpHeaders.CONTENT_DISPOSITION;
        String headerValue = "attachment;filename=User_Export.xlsx";
        response.setHeader(headerKey, headerValue);
        userService.exportUsersToExcel(response);
        return ResponseEntity
                .ok(Map.of("Message", "Export users to excel successfully"));
    }

    @Override
    public ResponseEntity<Resource> downloadUserExcelTemplate(HttpServletResponse response) {
        Resource resource = resourceLoader.getResource("classpath:templates/User-Template.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=User-Template.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
