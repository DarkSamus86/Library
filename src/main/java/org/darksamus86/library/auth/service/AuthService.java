package org.darksamus86.library.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.config.security.JwtTokenProvider;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.security.CustomUserDetailsService;
import org.darksamus86.library.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TokenStorageService tokenStorageService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Регистрация + автоматический вход (выдача токенов)
     */
    public AuthResponseDto register(UserRegistrationDto dto) {
        log.info("Registering new user: {}", dto.username());
        userService.register(dto); // Бизнес-логика в user-модуле
        return login(new LoginRequestDto(dto.username(), dto.password()));
    }

    /**
     * Аутентификация и генерация JWT
     */
    public AuthResponseDto login(LoginRequestDto dto) {
        log.debug("Authenticating user: {}", dto.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        tokenStorageService.saveToken(username, refreshToken); // логика сохранения токена в редис

        log.info("User authenticated successfully: {}", dto.username());
        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                86400000L // 24 часа в мс (можно вынести в конфиг)
        );
    }

    /**
     * Смена пароля + отзыв всех старых сессий
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        // 1. Проверяем текущий пароль и меняем на новый (через UserService)
        userService.changePassword(username, currentPassword, newPassword);

        // 2. Отзываем ВСЕ рефреш-токены пользователя (безопасность!)
        tokenStorageService.revokeToken(username);

        log.info("Password changed and all sessions revoked for user: {}", username);
    }

    /**
     * Обновление токенов
     */
    public AuthResponseDto refresh(String username, String refreshToken) {
        // 1. Проверяем токен в Redis
        if (!tokenStorageService.isValidToken(username, refreshToken)) {
            log.warn("Invalid refresh token for user: {}", username);
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // 2. Загружаем пользователя через UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 3. Генерируем новый Access Token
        String newAccessToken = jwtTokenProvider.generateToken(userDetails);

        log.debug("Access token refreshed for user: {}", username);
        return new AuthResponseDto(newAccessToken, refreshToken, "Bearer", 86400000L);
    }

    /**
     * Выход из системы (отзыв токена)
     */
    public void logout(String username) {
        tokenStorageService.revokeToken(username);
    }
}