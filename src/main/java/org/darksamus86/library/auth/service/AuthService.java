package org.darksamus86.library.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.config.security.JwtTokenProvider;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

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

        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        log.info("User authenticated successfully: {}", dto.username());
        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                86400000L // 24 часа в мс (можно вынести в конфиг)
        );
    }
}