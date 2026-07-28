package org.darksamus86.library.user.service;

import org.darksamus86.library.auth.service.TokenStorageService;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.mapper.UserMapper;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private RoleRepo roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenStorageService tokenStorageService;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUserRoles(new ArrayList<>());
        return user;
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_ShouldCreateUser() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");
        User user = createUser(1L, "testuser", "test@test.com");
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        UserRole userRole = UserRole.builder().user(user).role(role).build();
        user.getUserRoles().add(userRole);

        UserResponseDto expectedResponse = new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of("ROLE_USER"));

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponseDto result = userService.register(dto);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw when email already exists")
    void register_WhenEmailExists_ShouldThrow() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when username already exists")
    void register_WhenUsernameExists_ShouldThrow() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when ROLE_USER not found")
    void register_WhenRoleNotFound_ShouldThrow() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(createUser(1L, "testuser", "test@test.com"));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    @DisplayName("Should get user by id")
    void getUserById_ShouldReturnUser() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserResponseDto expected = new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of());

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponseDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(userRepository).findByIdWithRoles(1L);
    }

    @Test
    @DisplayName("Should throw when user not found by id")
    void getUserById_WhenNotFound_ShouldThrow() {
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should get current user from security context")
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserResponseDto expected = new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null)
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponseDto result = userService.getCurrentUser();

        assertThat(result).isEqualTo(expected);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw when authenticated user record no longer exists")
    void getCurrentUser_WhenRecordMissing_ShouldThrow() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("deleted-user", null)
        );
        when(userRepository.findByUsername("deleted-user")).thenReturn(Optional.empty());

        assertThatThrownBy(userService::getCurrentUser)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Logged-in user");
    }

    @Test
    @DisplayName("Should update current user profile")
    void updateProfile_ShouldUpdateFields() {
        User user = createUser(1L, "testuser", "test@test.com");
        UpdateProfileRequest request = new UpdateProfileRequest(
                "new@test.com", "newuser", "NewFirst", "NewLast"
        );
        UserResponseDto expected = new UserResponseDto(1L, "new@test.com", "newuser", "NewFirst", "NewLast", true, false, Set.of());

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        doAnswer(invocation -> {
            UpdateProfileRequest profile = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setEmail(profile.email());
            target.setUsername(profile.username());
            target.setFirstName(profile.firstName());
            target.setLastName(profile.lastName());
            return null;
        }).when(userMapper).applyProfileUpdates(request, user);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        UserResponseDto result = userService.updateProfile(1L, request);

        assertThat(result).isEqualTo(expected);
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getFirstName()).isEqualTo("NewFirst");
        assertThat(user.getLastName()).isEqualTo("NewLast");
        verify(userMapper).applyProfileUpdates(request, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw when profile email already exists")
    void updateProfile_WhenEmailExists_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");
        UpdateProfileRequest request = new UpdateProfileRequest(
                "existing@test.com", null, null, null
        );

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when profile username already exists")
    void updateProfile_WhenUsernameExists_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "existing-user", null, null
        );

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existing-user")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).applyProfileUpdates(any(), any());
    }

    @Test
    @DisplayName("Should not run uniqueness queries when email and username are unchanged")
    void updateProfile_WithUnchangedIdentity_ShouldSkipUniquenessChecks() {
        User user = createUser(1L, "testuser", "test@test.com");
        UpdateProfileRequest request = new UpdateProfileRequest(
                "test@test.com", "testuser", "Updated", null
        );
        UserResponseDto expected = new UserResponseDto(
                1L, "test@test.com", "testuser", "Updated", "User",
                true, false, Set.of()
        );

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponseDto result = userService.updateProfile(1L, request);

        assertThat(result).isEqualTo(expected);
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).existsByUsername(any());
        verify(userMapper).applyProfileUpdates(request, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw when current user is not found")
    void updateProfile_WhenUserNotFound_ShouldThrow() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "new@test.com", "newuser", null, null
        );
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(999L, request))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete user")
    void deleteUser_ShouldDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.hasPaymentMethods(1L)).thenReturn(false);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent user")
    void deleteUser_WhenNotFound_ShouldThrow() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw when user has payment methods")
    void deleteUser_WhenHasPaymentMethods_ShouldThrow() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.hasPaymentMethods(1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserDeletionForbiddenException.class);
    }

    @Test
    @DisplayName("Should change current user password by id and revoke refresh token")
    void changePasswordById_ShouldUpdatePasswordAndRevokeToken() {
        User user = createUser(1L, "testuser", "test@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncoded");
        when(userRepository.save(user)).thenReturn(user);

        userService.changePassword(1L, "currentPassword", "newPassword123");

        assertThat(user.getPasswordHash()).isEqualTo("newEncoded");
        verify(userRepository).save(user);
        verify(tokenStorageService).revokeToken("testuser");
    }

    @Test
    @DisplayName("Should reject password change by id when current password is wrong")
    void changePasswordById_WhenPasswordMismatch_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() ->
                userService.changePassword(1L, "wrongPassword", "newPassword123")
        ).isInstanceOf(PasswordMismatchException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(tokenStorageService);
    }

    @Test
    @DisplayName("Should throw when changing password for non-existent user")
    void changePassword_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(999L, "current", "newPassword123"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
