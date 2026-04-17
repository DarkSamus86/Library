package org.darksamus86.library.auth.dto.response;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}