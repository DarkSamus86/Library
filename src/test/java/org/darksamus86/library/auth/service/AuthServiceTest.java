package org.darksamus86.library.auth.service;

import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.config.security.JwtTokenProvider;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.security.CustomUserDetailsService;
import org.darksamus86.library.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private TokenStorageService tokenStorageService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register user and return tokens")
    void register_ShouldReturnAuthResponse() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        AuthResponseDto result = authService.register(dto);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        verify(userService).register(dto);
        verify(tokenStorageService).saveToken("testuser", "refreshToken");
    }

    @Test
    @DisplayName("Should login and return tokens")
    void login_ShouldReturnAuthResponse() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "password123");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        AuthResponseDto result = authService.login(dto);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        verify(tokenStorageService).saveToken("testuser", "refreshToken");
    }

    @Test
    @DisplayName("Should change password and revoke tokens")
    void changePassword_ShouldUpdatePasswordAndRevokeTokens() {
        authService.changePassword("testuser", "currentPassword", "newPassword123");

        verify(userService).changePassword("testuser", "currentPassword", "newPassword123");
        verify(tokenStorageService).revokeToken("testuser");
    }

    @Test
    @DisplayName("Should refresh token when valid")
    void refresh_WithValidToken_ShouldReturnNewAccessToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(tokenStorageService.isValidToken("testuser", "refreshToken")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("newAccessToken");

        AuthResponseDto result = authService.refresh("testuser", "refreshToken");

        assertThat(result.accessToken()).isEqualTo("newAccessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("Should throw when refresh token is invalid")
    void refresh_WithInvalidToken_ShouldThrow() {
        when(tokenStorageService.isValidToken("testuser", "invalidToken")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("testuser", "invalidToken"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    @DisplayName("Should throw and revoke token when disabled user tries to refresh")
    void refresh_WhenUserDisabled_ShouldThrowAndRevoke() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(false);
        when(tokenStorageService.isValidToken("testuser", "refreshToken")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        assertThatThrownBy(() -> authService.refresh("testuser", "refreshToken"))
                .isInstanceOf(DisabledException.class)
                .hasMessageContaining("Account is disabled");

        verify(tokenStorageService).revokeToken("testuser");
    }

    @Test
    @DisplayName("Should logout and revoke token")
    void logout_ShouldRevokeToken() {
        authService.logout("testuser");

        verify(tokenStorageService).revokeToken("testuser");
    }
}
