package com.fptacademy.training.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fptacademy.training.service.RoleService;
import com.fptacademy.training.service.dto.RoleDto;
import com.fptacademy.training.web.api.RoleResource;
import com.fptacademy.training.web.vm.RoleVM;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RoleResourceImpl implements RoleResource {
    private final RoleService roleService;

    @Override
    public ResponseEntity<List<RoleDto>> getAllPermission() {
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(roleService.getAllPermission());
    }

    @Override
    public ResponseEntity<List<RoleDto>> updatePermission(List<RoleVM> role) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roleService.updatePermission(role));
    }
}
