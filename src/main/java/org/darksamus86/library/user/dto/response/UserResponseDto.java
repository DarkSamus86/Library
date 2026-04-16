package org.darksamus86.library.user.dto.response;

public record UserResponseDto(
        Long id, String email, String username,
        String firstName, String lastName,
        Boolean isActive, Boolean isEmailVerified,
        java.util.Set<String> roles
) {}