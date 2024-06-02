package com.fptacademy.training.service.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.service.dto.PermissionDto;
import com.fptacademy.training.web.vm.PermissionVM;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionMapper {
    private final List<String> allowedObjects = List.of(
            "Syllabus",
            "Program",
            "Class",
            "Material",
            "User");

    private final List<String> allowedPermissions = List.of(
            "AccessDenied",
            "View",
            "Modify",
            "Create",
            "FullAccess");

    private final ModelMapper modelMapper;

    public String toString(PermissionDto permissionDto){
        Boolean isAllowed = false;

        String curObject = StringUtils.trimAllWhitespace(permissionDto.getObject().toLowerCase());
        for (String object : allowedObjects) {
            if (object.toLowerCase().equals(curObject)) {
                isAllowed = true;
                permissionDto.setObject(object);
                break;
            }
        }
        if (!isAllowed) 
            throw new ResourceBadRequestException("Object " + permissionDto.getObject() + " is not exist");

        isAllowed = false;
        String curPermission = StringUtils.trimAllWhitespace(permissionDto.getPermission().toLowerCase());
        for (String permission : allowedPermissions) {
            if (permission.toLowerCase().equals(curPermission)) {
                isAllowed = true;
                permissionDto.setPermission(permission);
                break;
            }
        }
        if (!isAllowed) 
            throw new ResourceBadRequestException("Permission " + permissionDto.getPermission() + " is not exist");
    
        return permissionDto.getObject() + "_" + permissionDto.getPermission();
    }

    public List<String> toStrings(List<PermissionDto> permissionDtos){
        return permissionDtos.stream().map(this::toString).toList();
    }

    public PermissionDto toDto(String permission){
        String[] permissionArray = permission.split("_");
        if (permissionArray.length != 2) 
            throw new ResourceBadRequestException("Permission " + permission + " is not valid");
        
        return new PermissionDto(permissionArray[0], permissionArray[1].replaceAll("(.)([A-Z])", "$1 $2"));
    }

    public List<PermissionDto> toDtos(List<String> permissions){
        return permissions.stream().map(this::toDto).toList();
    }

    public PermissionDto toDto(PermissionVM permissionVM){
        return new PermissionDto(permissionVM.object(), permissionVM.permission());
    }

    public List<PermissionDto> toDtosVM(List<PermissionVM> permissionVMs){
        return permissionVMs.stream().map(this::toDto).toList();
    }

    public List<String> toStringsVM(List<PermissionVM> permissionVMs){
        return toStrings(permissionVMs.stream().map(this::toDto).toList());
    }
}
