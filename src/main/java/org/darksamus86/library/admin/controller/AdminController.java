package org.darksamus86.library.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.admin.dto.request.RoleChangeRequest;
import org.darksamus86.library.admin.dto.request.UserStatusRequest;
import org.darksamus86.library.admin.dto.response.AdminDashboardResponse;
import org.darksamus86.library.admin.dto.response.AdminUserResponse;
import org.darksamus86.library.admin.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        log.info("GET /admin/dashboard");
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /admin/users | page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> getUser(@PathVariable Long id) {
        log.info("GET /admin/users/{}", id);
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<AdminUserResponse> changeUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody RoleChangeRequest request) {
        log.info("PUT /admin/users/{}/roles | roles={}", id, request.roles());
        return ResponseEntity.ok(adminService.changeUserRoles(id, request.roles()));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<AdminUserResponse> toggleUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request) {
        log.info("PATCH /admin/users/{}/status | active={}", id, request.isActive());
        return ResponseEntity.ok(adminService.toggleUserStatus(id, request.isActive()));
    }
}
