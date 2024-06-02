package com.fptacademy.training.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.enumeration.RoleName;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.exception.ResourceNotFoundException;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.service.dto.RoleDto;
import com.fptacademy.training.service.mapper.PermissionMapper;
import com.fptacademy.training.service.mapper.RoleMapper;
import com.fptacademy.training.web.vm.RoleVM;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoleService {
    @Autowired
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public Role getRoleByName(String name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + name + " not found"));
    }

    public Role getRoleByID(long id) {
        return roleRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type role " + id + " not found"));
    }

    public List<RoleDto> getAllPermission() {
        return roleMapper.toDtos(roleRepository.findAll());
    }

    public List<RoleDto> updatePermission(List<RoleVM> roles) {
        Map<String, String> mapRole = new HashMap<>();
        for (RoleName roleName : RoleName.values()) {
            mapRole.put(StringUtils.trimAllWhitespace(roleName.toString().toLowerCase()), roleName.toString());
        }
        for (RoleVM role : roles){
            String shortRoleName = StringUtils.trimAllWhitespace(role.name().toLowerCase());
            if (mapRole.get(shortRoleName) == null) {
                throw new ResourceBadRequestException("Role " + role.name() + " not found");
            }
            if (mapRole.get(shortRoleName) == "") {
                throw new ResourceBadRequestException("Role " + role.name() + " is duplicated");
            }
            
            String roleName = mapRole.get(shortRoleName);
            if (roleName.equals(RoleName.SUPER_ADMIN.toString())) {
                throw new ResourceBadRequestException("Don't allow to update permissions of Super Admin");
            }
            mapRole.put(shortRoleName, "");

            List<String> listPermission = permissionMapper.toStringsVM(role.permissions());
            updatePermission(roleName, listPermission);
        }
        return roleMapper.toDtos(roleRepository.findAll());
    }

    public void updatePermission(String role, List<String> permissions) {
        Role roleEntity = roleRepository
                .findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + role + " not found"));
        ;

        Map<String, String> mapPermission = new HashMap<>();
        for (String permission : permissions) {
            String object = permission.split("_")[0];
            mapPermission.put(object, permission);
        }

        List<String> curPermissions = new ArrayList<>(roleEntity.getPermissions());
        for (int i = 0; i < curPermissions.size(); i++) {
            String object = curPermissions.get(i).split("_")[0];
            
            if (mapPermission.get(object) != null)
                curPermissions.set(i, mapPermission.get(object));
        }
        roleEntity.setPermissions(curPermissions);
        roleRepository.save(roleEntity);
    }
}
