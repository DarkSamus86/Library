package org.darksamus86.library.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.user.common.exceptions.UserNotFoundException;
import org.darksamus86.library.user.common.handler.UserExceptionHandler;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.request.UserUpdateDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);
        UserExceptionHandler handler = new UserExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUser_ShouldReturn200() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of("ROLE_USER"));
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getUser_ShouldReturn404_WhenNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException("999"));

        mockMvc.perform(get("/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        UserUpdateDto dto = new UserUpdateDto(null, null, null, "currentPassword", "NewName", "NewLast");
        UserResponseDto response = new UserResponseDto(1L, "test@test.com", "testuser", "NewName", "NewLast", true, false, Set.of("ROLE_USER"));

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value("NewLast"));
    }

    @Test
    void updateUser_ShouldReturn401_WhenPasswordMismatch() throws Exception {
        UserUpdateDto dto = new UserUpdateDto(null, null, null, "wrongPassword", null, null);
        when(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenThrow(new org.darksamus86.library.user.common.exceptions.PasswordMismatchException());

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}
