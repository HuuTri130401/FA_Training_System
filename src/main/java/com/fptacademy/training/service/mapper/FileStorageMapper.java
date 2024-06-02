package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.FileStorage;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.service.dto.FileStorageDto;
import com.fptacademy.training.service.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileStorageMapper {
    private final ModelMapper modelMapper;

    public FileStorageDto toDto(FileStorage storage) {
        if (storage == null) {
            return null;
        }
        FileStorageDto dto = modelMapper.map(storage, FileStorageDto.class);
        dto.setTime(storage.getDate());
        return dto;
    }

    public List<FileStorageDto> toDtos(List<FileStorage> storages) {
        return storages.stream().map(this::toDto).toList();
    }
}
