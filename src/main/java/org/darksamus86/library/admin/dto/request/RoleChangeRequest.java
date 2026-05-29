package org.darksamus86.library.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record RoleChangeRequest(
        @NotEmpty(message = "At least one role is required")
        Set<String> roles
) {}
