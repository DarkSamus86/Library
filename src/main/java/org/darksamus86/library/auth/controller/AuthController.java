package org.darksamus86.library.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.darksamus86.library.auth.dto.request.ChangePasswordRequestDto;
import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.request.RefreshRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.auth.service.AuthService;
import org.darksamus86.library.config.security.JwtTokenProvider;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid ChangePasswordRequestDto request,
            Authentication authentication) {

        String username = authentication.getName();
        authService.changePassword(username, request.currentPassword(), request.newPassword());
    }

    /**
     * Обновление Access-токена по Refresh-токену
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshRequestDto request) {
        // Извлекаем username из токена (или принимаем в теле запроса)
        String username = jwtTokenProvider.extractUsername(request.refreshToken());
        var response = authService.refresh(username, request.refreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Выход из системы
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication) {
        String username = authentication.getName();
        authService.logout(username);
    }

    /**
     * Тестовый эндпоинт для проверки валидности JWT
     * Доступен только аутентифицированным пользователям
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok("JWT is valid! Token belongs to: " + token.substring(0, 10) + "...");
    }
}