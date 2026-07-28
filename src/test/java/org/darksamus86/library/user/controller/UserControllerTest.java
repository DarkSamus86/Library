package org.darksamus86.library.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.user.common.exceptions.UserNotFoundException;
import org.darksamus86.library.user.common.handler.UserExceptionHandler;
import org.darksamus86.library.user.dto.request.ChangePasswordRequest;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.security.CustomUserDetails;
import org.darksamus86.library.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CustomUserDetails principal;

    @BeforeEach
    void setUp() {
        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("testuser");
        authenticatedUser.setPasswordHash("hashedPassword");
        authenticatedUser.setIsActive(true);
        authenticatedUser.setUserRoles(List.of());
        principal = new CustomUserDetails(authenticatedUser);

        UserController controller = new UserController(userService);
        UserExceptionHandler handler = new UserExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .setCustomArgumentResolvers(authenticationPrincipalResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    private HandlerMethodArgumentResolver authenticationPrincipalResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(CustomUserDetails.class);
            }

            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                return principal;
            }
        };
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
    void getCurrentUser_ShouldReturn200() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of("ROLE_USER"));
        when(userService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateCurrentUser_ShouldUseAuthenticatedUserIdAndReturn200() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "new@test.com", "newuser", "NewName", "NewLast"
        );
        UserResponseDto response = new UserResponseDto(
                1L, "new@test.com", "newuser", "NewName", "NewLast",
                true, false, Set.of("ROLE_USER")
        );

        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value("NewLast"));

        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    void changePassword_ShouldUseAuthenticatedUserIdAndReturn204() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "currentPassword", "newPassword123"
        );

        mockMvc.perform(put("/user/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(1L, "currentPassword", "newPassword123");
    }

    @Test
    void changePassword_ShouldReturn400_WhenNewPasswordIsTooShort() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "currentPassword", "short"
        );

        mockMvc.perform(put("/user/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void changePassword_ShouldReturn400_WhenCurrentPasswordIsBlank() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("", "newPassword123");

        mockMvc.perform(put("/user/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void updateCurrentUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "not-an-email", "validuser", null, null
        );

        mockMvc.perform(patch("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void updateCurrentUser_ShouldReturn400_WhenUsernameIsTooShort() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "ab", null, null
        );

        mockMvc.perform(patch("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void operationsByArbitraryId_ShouldRemainAdminOnly() throws Exception {
        PreAuthorize getUserPolicy = UserController.class
                .getMethod("getUser", Long.class)
                .getAnnotation(PreAuthorize.class);
        PreAuthorize deleteUserPolicy = UserController.class
                .getMethod("deleteUser", Long.class)
                .getAnnotation(PreAuthorize.class);

        assertThat(getUserPolicy).isNotNull();
        assertThat(getUserPolicy.value()).contains("ROLE_ADMIN");
        assertThat(deleteUserPolicy).isNotNull();
        assertThat(deleteUserPolicy.value()).contains("ROLE_ADMIN");
    }
}
