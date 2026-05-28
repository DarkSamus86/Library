package org.darksamus86.library.admin.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminUserResponse(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName,
        Boolean isActive,
        Boolean isEmailVerified,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
