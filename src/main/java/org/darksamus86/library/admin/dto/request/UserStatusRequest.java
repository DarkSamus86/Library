package org.darksamus86.library.admin.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(
        @NotNull(message = "isActive is required")
        Boolean isActive
) {}
