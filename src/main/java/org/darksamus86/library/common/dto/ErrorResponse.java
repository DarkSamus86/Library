package org.darksamus86.library.common.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}