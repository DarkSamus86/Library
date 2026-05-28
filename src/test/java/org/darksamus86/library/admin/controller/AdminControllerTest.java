package org.darksamus86.library.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.admin.common.handler.AdminExceptionHandler;
import org.darksamus86.library.admin.dto.response.AdminDashboardResponse;
import org.darksamus86.library.admin.dto.response.AdminUserResponse;
import org.darksamus86.library.admin.dto.request.RoleChangeRequest;
import org.darksamus86.library.admin.dto.request.UserStatusRequest;
import org.darksamus86.library.admin.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AdminController controller = new AdminController(adminService);
        AdminExceptionHandler handler = new AdminExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(handler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getDashboard_ShouldReturn200() throws Exception {
        AdminDashboardResponse response = new AdminDashboardResponse(
                5, 4, 20, 18, Map.of("ROLE_ADMIN", 1L, "ROLE_USER", 4L)
        );
        when(adminService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(5))
                .andExpect(jsonPath("$.activeUsers").value(4))
                .andExpect(jsonPath("$.totalBooks").value(20))
                .andExpect(jsonPath("$.usersByRole.ROLE_ADMIN").value(1));
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        AdminUserResponse userResponse = new AdminUserResponse(
                1L, "user@test.com", "testuser",
                "Test", "User", true, true,
                Set.of("ROLE_USER"),
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 5, 28, 12, 0)
        );
        Page<AdminUserResponse> page = new PageImpl<>(List.of(userResponse), PageRequest.of(0, 20), 1);
        when(adminService.getAllUsers(any())).thenReturn(page);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.content[0].roles").isArray())
                .andExpect(jsonPath("$.content[0].roles[?(@ == 'ROLE_USER')]").exists())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        AdminUserResponse response = new AdminUserResponse(
                1L, "admin@test.com", "admin",
                "Admin", "User", true, true,
                Set.of("ROLE_ADMIN"),
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 5, 28, 12, 0)
        );
        when(adminService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[?(@ == 'ROLE_ADMIN')]").exists());
    }

    @Test
    void changeUserRoles_ShouldReturn200() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(Set.of("ROLE_ADMIN", "ROLE_USER"));
        AdminUserResponse response = new AdminUserResponse(
                1L, "user@test.com", "user",
                null, null, true, true,
                Set.of("ROLE_ADMIN", "ROLE_USER"),
                null, null
        );
        when(adminService.changeUserRoles(eq(1L), eq(Set.of("ROLE_ADMIN", "ROLE_USER"))))
                .thenReturn(response);

        mockMvc.perform(put("/admin/users/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[?(@ == 'ROLE_ADMIN')]").exists())
                .andExpect(jsonPath("$.roles[?(@ == 'ROLE_USER')]").exists());
    }

    @Test
    void changeUserRoles_WhenEmptyRoles_ShouldReturn400() throws Exception {
        RoleChangeRequest request = new RoleChangeRequest(Set.of());

        mockMvc.perform(put("/admin/users/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void toggleUserStatus_ShouldReturn200() throws Exception {
        UserStatusRequest request = new UserStatusRequest(false);
        AdminUserResponse response = new AdminUserResponse(
                1L, "user@test.com", "user",
                null, null, false, true,
                Set.of("ROLE_USER"),
                null, null
        );
        when(adminService.toggleUserStatus(eq(1L), eq(false))).thenReturn(response);

        mockMvc.perform(patch("/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void toggleUserStatus_WhenNullBody_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDashboard_WhenIllegalArgument_ShouldReturn400() throws Exception {
        when(adminService.getDashboard())
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }
}
