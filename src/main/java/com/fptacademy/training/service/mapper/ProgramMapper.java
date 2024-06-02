package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.*;
import com.fptacademy.training.service.dto.ProgramDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ProgramMapper {
    private final ModelMapper modelMapper;

    public ProgramDto toDto(Program program) {
        if (program == null) {
            return null;
        }
        ProgramDto dto = modelMapper.map(program, ProgramDto.class);
        int days = (int)program.getSyllabuses()
                .stream()
                .mapToLong(Syllabus::getDuration)
                .sum();
        double hours = program.getSyllabuses()
                .stream()
                .flatMap(s -> s.getSessions().stream())
                .flatMap(s -> s.getUnits().stream())
                .mapToDouble(Unit::getTotalDurationLesson)
                .sum() / 60.;
        dto.setCreatedBy(new ProgramDto.Creator(
                program.getCreatedBy().getId(),
                program.getCreatedBy().getFullName(),
                program.getCreatedBy().getCode()));
        dto.setLastModifiedBy(new ProgramDto.Creator(
                program.getLastModifiedBy().getId(),
                program.getLastModifiedBy().getFullName(),
                program.getLastModifiedBy().getCode()));
        dto.setDurationInDays(days);
        dto.setDurationInHours(hours);
        return dto;
    }

    public List<ProgramDto> toDtos(List<Program> programs) {
        if (programs == null) {
            return null;
        }
        return programs.stream().filter(Objects::nonNull).map(this::toDto).toList();
    }
}
