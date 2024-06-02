package com.fptacademy.training.web.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
public record RoleVM(
        @Schema(example = "Class Admin")
        String name,
        @Schema(defaultValue = "[{\"object\":\"Syllabus\",\"permission\":\"View\"},{\"object\":\"Program\",\"permission\":\"Create\"},{\"object\":\"Class\",\"permission\":\"Modify\"},{\"object\":\"Material\",\"permission\":\"FullAccess\"},{\"object\":\"User\",\"permission\":\"AccessDenied\"}]")
        List<PermissionVM> permissions
) {
}
