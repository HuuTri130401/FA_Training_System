package com.fptacademy.training.web.vm;

import io.swagger.v3.oas.annotations.media.Schema;

public record PermissionVM(
    @Schema(defaultValue = "Syllabus")
    String object,
    @Schema(defaultValue = "View")
    String permission
) {
}
