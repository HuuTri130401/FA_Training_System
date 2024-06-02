package com.fptacademy.training.web.vm;

import com.fptacademy.training.service.dto.ProgramDto;

import java.util.List;

public record ProgramListResponseVM(long total, List<ProgramDto> programs) {
}
