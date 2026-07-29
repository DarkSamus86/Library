package org.darksamus86.library.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.user.dto.request.ChangePasswordRequest;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.security.CustomUserDetails;
import org.darksamus86.library.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        log.info("GET /user/{} | Fetching user info", id);
        UserResponseDto response = userService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.info("Get current user");
        UserResponseDto response = userService.getCurrentUser();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateCurrentUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request
            ) {
        return ResponseEntity.ok(
                userService.updateProfile(principal.getId(), request)
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
                                               @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Update user password");
        userService.changePassword(
                principal.getId(),
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /user/{} | Admin delete request", id);
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
