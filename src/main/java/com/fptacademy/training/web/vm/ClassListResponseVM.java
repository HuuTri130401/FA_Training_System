package com.fptacademy.training.web.vm;

import com.fptacademy.training.service.dto.ClassDto;

import java.util.List;

public record ClassListResponseVM(
        int totalPages,
        int totalElements,
        int size,
        int page,
        List<ClassDto> content
) {
}
