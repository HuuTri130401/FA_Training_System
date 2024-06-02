package com.fptacademy.training.web.vm;

import com.fptacademy.training.service.dto.ProgramDto;

import java.util.List;

public record ProgramExcelImportResponseVM(List<String> duplicateProgramNames, List<ProgramDto> programs) {
}
