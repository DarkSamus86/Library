package org.darksamus86.library.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.auth.dto.request.ChangePasswordRequestDto;
import org.darksamus86.library.auth.dto.request.LoginRequestDto;
import org.darksamus86.library.auth.dto.request.RefreshRequestDto;
import org.darksamus86.library.auth.dto.response.AuthResponseDto;
import org.darksamus86.library.auth.service.AuthService;
import org.darksamus86.library.config.GlobalExceptionHandler;
import org.darksamus86.library.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService, jwtTokenProvider);
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ShouldReturn201() throws Exception {
        AuthResponseDto response = new AuthResponseDto("accessToken", "refreshToken", "Bearer", 86400000L);
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        AuthResponseDto response = new AuthResponseDto("accessToken", "refreshToken", "Bearer", 86400000L);
        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    void login_ShouldReturn400_WhenBlankFields() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_ShouldReturn204() throws Exception {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("currentPassword", "newPassword123");
        var auth = new UsernamePasswordAuthenticationToken("testuser", "password");

        doNothing().when(authService).changePassword(eq("testuser"), eq("currentPassword"), eq("newPassword123"));

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(auth))
                .andExpect(status().isNoContent());

        verify(authService).changePassword("testuser", "currentPassword", "newPassword123");
    }

    @Test
    void changePassword_ShouldReturn400_WhenBlankFields() throws Exception {
        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"\",\"newPassword\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_ShouldReturn200() throws Exception {
        AuthResponseDto response = new AuthResponseDto("newAccessToken", "refreshToken", "Bearer", 86400000L);
        when(jwtTokenProvider.extractUsername("refreshToken")).thenReturn("testuser");
        when(authService.refresh("testuser", "refreshToken")).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refreshToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"));
    }

    @Test
    void refresh_ShouldReturn400_WhenBlankToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_ShouldReturn204() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        doNothing().when(authService).logout("testuser");

        mockMvc.perform(post("/auth/logout")
                        .principal(auth))
                .andExpect(status().isNoContent());

        verify(authService).logout("testuser");
    }
}
