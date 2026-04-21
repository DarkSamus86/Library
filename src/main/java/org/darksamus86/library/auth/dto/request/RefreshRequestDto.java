package org.darksamus86.library.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(
        @NotBlank String refreshToken
) {}
