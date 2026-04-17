package org.darksamus86.library.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.auth.service.AuthService;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
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