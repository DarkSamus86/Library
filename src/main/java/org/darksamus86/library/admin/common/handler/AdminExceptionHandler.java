package org.darksamus86.library.admin.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "org.darksamus86.library.admin")
@Slf4j
public class AdminExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Admin bad request: {}", e.getMessage());
        return ResponseEntity.status(400)
                .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now()));
    }
}
