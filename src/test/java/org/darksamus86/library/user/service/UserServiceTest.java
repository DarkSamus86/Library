package org.darksamus86.library.user.service;

import org.darksamus86.library.notification.event.UserRegisteredEvent;
import org.darksamus86.library.notification.publisher.UserEventPublisher;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.request.UserUpdateDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.mapper.UserMapper;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

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
    void register_ShouldCreateUserAndPublishEvent() {
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
        verify(eventPublisher).publishUserRegistered(any(UserRegisteredEvent.class));
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
    @DisplayName("Should update user profile")
    void updateUser_ShouldUpdateFields() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserUpdateDto dto = new UserUpdateDto(null, null, null, "currentPassword", "NewFirst", "NewLast");
        UserResponseDto expected = new UserResponseDto(1L, "test@test.com", "testuser", "NewFirst", "NewLast", true, false, Set.of());

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        UserResponseDto result = userService.updateUser(1L, dto);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw when current password is wrong")
    void updateUser_WhenPasswordMismatch_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserUpdateDto dto = new UserUpdateDto(null, null, null, "wrongPassword", null, null);

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    @DisplayName("Should update email and reset verification")
    void updateUser_WhenEmailChanged_ShouldResetVerification() {
        User user = createUser(1L, "testuser", "test@test.com");
        user.setIsEmailVerified(true);
        UserUpdateDto dto = new UserUpdateDto("new@test.com", null, null, "currentPassword", null, null);

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponseDto(1L, "new@test.com", "testuser", "Test", "User", true, false, Set.of()));

        userService.updateUser(1L, dto);

        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getIsEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should throw when new email already exists")
    void updateUser_WhenNewEmailExists_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserUpdateDto dto = new UserUpdateDto("existing@test.com", null, null, "currentPassword", null, null);

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should update username")
    void updateUser_WhenUsernameChanged_ShouldUpdate() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserUpdateDto dto = new UserUpdateDto(null, "newuser", null, "currentPassword", null, null);

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponseDto(1L, "test@test.com", "newuser", "Test", "User", true, false, Set.of()));

        userService.updateUser(1L, dto);

        assertThat(user.getUsername()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("Should update password")
    void updateUser_WhenPasswordProvided_ShouldEncodeAndSet() {
        User user = createUser(1L, "testuser", "test@test.com");
        UserUpdateDto dto = new UserUpdateDto(null, null, "newPassword123", "currentPassword", null, null);

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponseDto(1L, "test@test.com", "testuser", "Test", "User", true, false, Set.of()));

        userService.updateUser(1L, dto);

        assertThat(user.getPasswordHash()).isEqualTo("newEncodedPassword");
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
    @DisplayName("Should change password")
    void changePassword_ShouldUpdatePassword() {
        User user = createUser(1L, "testuser", "test@test.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.changePassword("testuser", "currentPassword", "newPassword123");

        assertThat(user.getPasswordHash()).isEqualTo("newEncoded");
    }

    @Test
    @DisplayName("Should throw when changing password with wrong current password")
    void changePassword_WhenWrongCurrentPassword_ShouldThrow() {
        User user = createUser(1L, "testuser", "test@test.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("testuser", "wrongPassword", "newPassword123"))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    @DisplayName("Should throw when changing password for non-existent user")
    void changePassword_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword("nonexistent", "current", "new"))
                .isInstanceOf(UserNotFoundException.class);
    }
}
