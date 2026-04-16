package org.darksamus86.library.user.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.response.ErrorResponse; // 👈 Ваш DTO
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "org.darksamus86.library.user")
@Order(10)
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex, HttpServletRequest req) {
        log.warn("404 Not Found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex, req);
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            RoleAlreadyAssignedException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(UserModuleException ex, HttpServletRequest req) {
        log.warn("409 Conflict: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex, req);
    }

    @ExceptionHandler({
            InvalidEmailFormatException.class,
            WeakPasswordException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(UserModuleException ex, HttpServletRequest req) {
        log.warn("400 Bad Request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex, req);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(PasswordMismatchException ex, HttpServletRequest req) {
        log.warn("401 Unauthorized: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex, req);
    }

    @ExceptionHandler({
            AccountNotVerifiedException.class,
            AccountLockedException.class,
            CannotRemoveLastAdminRoleException.class,
            UserDeletionForbiddenException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(UserModuleException ex, HttpServletRequest req) {
        log.warn("403 Forbidden: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, ex, req);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServerRoleError(RoleNotFoundException ex, HttpServletRequest req) {
        log.error("500 Server Error (Config): {}", ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex, req);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleGone(VerificationTokenExpiredException ex, HttpServletRequest req) {
        log.warn("410 Gone: {}", ex.getMessage());
        return build(HttpStatus.GONE, ex, req);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleConcurrency(OptimisticLockException ex, HttpServletRequest req) {
        log.warn("409 Conflict (Concurrent Modification): {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex, req);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        status.value(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        req.getRequestURI()
                )
        );
    }
}